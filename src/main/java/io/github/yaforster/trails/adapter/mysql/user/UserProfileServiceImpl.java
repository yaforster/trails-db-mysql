package io.github.yaforster.trails.adapter.mysql.user;

import io.github.yaforster.trails.adapter.mysql.SpringPageMapper;
import io.github.yaforster.trails.app.services.UserProfileService;
import io.github.yaforster.trails.core.BinaryContent;
import io.github.yaforster.trails.core.PagedResult;
import io.github.yaforster.trails.core.data.TrailsScreenshotFile;
import io.github.yaforster.trails.core.user.UserProfile;
import io.github.yaforster.trails.core.user.UserProfilePicture;
import io.github.yaforster.trails.core.user.UserProfilePictureResource;
import io.github.yaforster.trails.core.user.UserProfileUpdate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository repository;

    @Override
    @Transactional
    public UserProfile getOrCreateProfile(UUID userId) {
        UserProfileEntity entity = getOrCreateEntity(userId);
        return toDomain(entity);
    }

    @Override
    @Transactional
    public UserProfile updateProfile(UserProfileChange profileChange) {
        UserProfileEntity entity = getOrCreateEntity(profileChange.userId());
        applyProfileUpdate(entity, profileChange.update());
        UserProfileEntity storedProfile = repository.save(entity);
        return toDomain(storedProfile);
    }

    private void applyProfileUpdate(UserProfileEntity entity, UserProfileUpdate update) {
        String firstName = update.firstName()
                                 .orElse(null);
        String lastName = update.lastName()
                                .orElse(null);
        String email = update.email()
                             .orElse(null);
        String phoneNumber = update.phoneNumber()
                                   .orElse(null);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setEmail(email);
        entity.setPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfilePicture> getProfilePicture(UUID userId) {
        return repository.findById(userId)
                         .filter(entity -> entity.getProfilePictureContent() != null)
                         .map(this::toProfilePicture);
    }

    private UserProfilePicture toProfilePicture(UserProfileEntity entity) {
        BinaryContent content = new BinaryContent(entity.getProfilePictureContent());
        return new UserProfilePicture(content, entity.getProfilePictureFileName(),
                entity.getProfilePictureContentType(), entity.getProfilePictureUpdatedAt());
    }

    @Override
    @Transactional
    public UserProfilePictureResource updateProfilePicture(UserProfilePictureChange pictureChange) {
        UserProfileEntity entity = getOrCreateEntity(pictureChange.userId());
        OffsetDateTime updatedAt = OffsetDateTime.now();
        TrailsScreenshotFile file = pictureChange.file();
        String contentType = file.imageType()
                                 .contentType();
        entity.setProfilePictureFileName(file.filename());
        entity.setProfilePictureContentType(contentType);
        entity.setProfilePictureContent(file.content());
        entity.setProfilePictureUpdatedAt(updatedAt);
        repository.save(entity);
        return new UserProfilePictureResource(contentType, updatedAt);
    }

    @Override
    @Transactional
    public void deleteProfilePicture(UUID userId) {
        UserProfileEntity entity = getOrCreateEntity(userId);
        entity.setProfilePictureFileName(null);
        entity.setProfilePictureContentType(null);
        entity.setProfilePictureContent(null);
        entity.setProfilePictureUpdatedAt(null);
        repository.save(entity);
    }

    @Override
    @Transactional
    public void deleteUserData(UUID userId) {
        repository.findById(userId)
                  .ifPresent(repository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<UserProfile> listUsers(UserPage userPage) {
        Pageable pageable = PageRequest.of(userPage.page(), userPage.size());
        Page<UserProfileEntity> entities = repository.findAll(pageable);
        Page<UserProfile> profiles = entities.map(this::toDomain);
        return SpringPageMapper.toPagedResult(profiles);
    }

    private UserProfileEntity getOrCreateEntity(UUID userId) {
        return repository.findById(userId)
                         .orElseGet(() -> createProfile(userId));
    }

    private UserProfileEntity createProfile(UUID userId) {
        UserProfileEntity entity = new UserProfileEntity().setUserId(userId);
        return repository.save(entity);
    }

    private UserProfile toDomain(UserProfileEntity entity) {
        return new UserProfile(entity.getUserId(), entity.getFirstName(), entity.getLastName(), entity.getEmail(),
                entity.getPhoneNumber(), entity.getProfilePictureContent() != null);
    }

}

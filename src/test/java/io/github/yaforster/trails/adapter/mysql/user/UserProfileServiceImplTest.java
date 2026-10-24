package io.github.yaforster.trails.adapter.mysql.user;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.app.services.UserProfileService.UserPage;
import io.github.yaforster.trails.app.services.UserProfileService.UserProfilePictureChange;
import io.github.yaforster.trails.app.services.UserProfileService.UserProfileChange;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.data.TrailsScreenshotFile;
import io.github.yaforster.trails.core.user.UserProfile;
import io.github.yaforster.trails.core.user.UserProfilePicture;
import io.github.yaforster.trails.core.user.UserProfilePictureResource;
import io.github.yaforster.trails.core.user.UserProfileUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserProfileServiceImplTest extends TrailsTest {

    private final UserProfileRepository repository = mock(UserProfileRepository.class);
    private final UserProfileServiceImpl service = new UserProfileServiceImpl(repository);

    @Test
    void getOrCreateProfile_shouldCreateIdOnlyProfile_whenMissing() {
        UUID userId = UUID.randomUUID();
        when(repository.findById(userId)).thenReturn(Optional.empty());
        when(repository.save(any(UserProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfile result = service.getOrCreateProfile(userId);

        assertEquals(userId, result.userId());
        verify(repository).save(any(UserProfileEntity.class));
    }

    @Test
    void updateProfile_shouldStoreUpdateValues() {
        UUID userId = UUID.randomUUID();
        UserProfileEntity entity = new UserProfileEntity().setUserId(userId);
        UserProfileUpdate update = new UserProfileUpdate(Optional.of("Ada"), Optional.of("Lovelace"),
                Optional.of("ada@example.org"), Optional.of("+491701234567"));
        when(repository.findById(userId)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        UserProfile result = service.updateProfile(new UserProfileChange(userId, update));

        assertEquals("Ada", result.firstName());
        verify(repository).save(entity);
    }

    @Test
    void updateProfile_shouldOverwriteExistingValueWithNull() {
        UUID userId = UUID.randomUUID();
        UserProfileEntity entity = new UserProfileEntity().setUserId(userId)
                                                          .setFirstName("Ada");
        UserProfileUpdate update = new UserProfileUpdate(Optional.empty(), Optional.of("Lovelace"),
                Optional.of("ada@example.org"), Optional.of("+491701234567"));
        when(repository.findById(userId)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        UserProfile result = service.updateProfile(new UserProfileChange(userId, update));

        assertNull(result.firstName());
    }

    @Test
    void getProfilePicture_shouldReturnEmpty_whenNoPictureExists() {
        UUID userId = UUID.randomUUID();
        when(repository.findById(userId)).thenReturn(Optional.of(new UserProfileEntity().setUserId(userId)));

        Optional<UserProfilePicture> result = service.getProfilePicture(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateProfilePicture_shouldStorePictureAndReturnResource() {
        UUID userId = UUID.randomUUID();
        UserProfileEntity entity = new UserProfileEntity().setUserId(userId);
        TrailsScreenshotFile file = screenshotFile("avatar.jpg", jpegBytes());
        when(repository.findById(userId)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        UserProfilePictureResource result = service.updateProfilePicture(new UserProfilePictureChange(userId, file));

        assertEquals("image/jpeg", result.contentType());
        verify(repository).save(entity);
    }

    @Test
    void deleteProfilePicture_shouldClearPictureFields() {
        UUID userId = UUID.randomUUID();
        UserProfileEntity entity = new UserProfileEntity().setUserId(userId)
                                                          .setProfilePictureContent(new byte[]{1})
                                                          .setProfilePictureContentType("image/jpeg")
                                                          .setProfilePictureFileName("avatar.jpg");
        when(repository.findById(userId)).thenReturn(Optional.of(entity));

        service.deleteProfilePicture(userId);

        assertNull(entity.getProfilePictureContent());
        verify(repository).save(entity);
    }

    @Test
    void deleteUserData_shouldDeleteExistingProfile() {
        UUID userId = UUID.randomUUID();
        UserProfileEntity entity = new UserProfileEntity().setUserId(userId);
        when(repository.findById(userId)).thenReturn(Optional.of(entity));

        service.deleteUserData(userId);

        verify(repository).delete(entity);
    }

    @Test
    void deleteUserData_shouldIgnoreMissingProfile() {
        UUID userId = UUID.randomUUID();
        when(repository.findById(userId)).thenReturn(Optional.empty());

        service.deleteUserData(userId);

        verify(repository, never()).delete(any());
    }

    @Test
    void listUsers_shouldReturnPagedProfiles() {
        UUID userId = UUID.randomUUID();
        UserProfileEntity entity = new UserProfileEntity().setUserId(userId)
                                                          .setEmail("admin@example.org");
        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(
                new PageImpl<>(List.of(entity), PageRequest.of(1, 10), 30));

        PagedResult<UserProfile> result = service.listUsers(new UserPage(1, 10));

        assertEquals(userId, result.getContent()
                                   .getFirst()
                                   .userId());
    }

}

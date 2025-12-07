package kr.rahul.moneyManager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.rahul.moneyManager.dto.AuthDTO;
import kr.rahul.moneyManager.dto.ProfileDTO;
import kr.rahul.moneyManager.entity.ProfileEntity;
import kr.rahul.moneyManager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    

    @Value("${app.activation.url}")
    private String activationBaseUrl;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {

        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        // build activation link
       // String activationLink = activationBaseUrl + "/activation?token=" + newProfile.getActivationToken();
        String activationLink = activationBaseUrl + "/activate?token=" + newProfile.getActivationToken();


        String subject = "Activate your Money Manager account";
        String body = "Click on the following link to activate your account:\n" + activationLink;

        emailService.sendEmail(newProfile.getEmail(), subject, body);

        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .imageUrl(profileDTO.getImageUrl())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .createAt(profileDTO.getCreateAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .imageUrl(profileEntity.getImageUrl())
                .createAt(profileEntity.getCreateAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
    
    public List<ProfileDTO> getProfiles() {
        List<ProfileEntity> profiles = profileRepository.findAll();
        List<ProfileDTO> profileDTOS = new ArrayList<>();
        for(ProfileEntity profileEntity : profiles){
            profileDTOS.add(toDTO(profileEntity));
        }
        return profileDTOS;
    }
    
    public boolean activateProfile(String activationToken) {
    	return profileRepository.findByActivationToken(activationToken)
    			.map(profile ->{
    				profile.setIsActive(true);
    				profileRepository.save(profile);
    				return true;
    			}).orElse(false);
    }
    
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }
    
    public ProfileEntity getCurrentProfile(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("Profile not found"));
    }
    
    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentProfile = null;
        if(email==null){
            currentProfile = getCurrentProfile();
        }
        else{
            currentProfile = profileRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Profile not found with this email "+email));
        }

        return toDTO(currentProfile);
    }
    
    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));

            String jwtToken = jwtService.generateToken(authDTO.getEmail());
                 return Map.of("token",jwtToken,"user",getPublicProfile(authDTO.getEmail()));
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

}

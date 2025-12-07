package kr.rahul.moneyManager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.rahul.moneyManager.dto.AuthDTO;
import kr.rahul.moneyManager.dto.ProfileDTO;
import kr.rahul.moneyManager.service.ProfileService;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class ProfileController {
    
    private final ProfileService profileService;
    
    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registreProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registreProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registreProfile);
    }
    
    @GetMapping("/profiles")
    public ResponseEntity<List<ProfileDTO>> getProfiles(){
        List<ProfileDTO> profileDTOs = profileService.getProfiles();
        return new ResponseEntity<>(profileDTOs,HttpStatus.FOUND);
    }

    
    
    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
        try{
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return new ResponseEntity<>(Map.of("message","Account is not active. Please activate your account first"),HttpStatus.FORBIDDEN);
            }
            Map<String,Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam  String token){
        boolean isActivated = profileService.activateProfile(token);

        return isActivated ? new ResponseEntity<>("Profile Activated Successfully",HttpStatus.OK) : new ResponseEntity<>("Profile Activation Failed",HttpStatus.CONFLICT);
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getPublicProfile(){
        return new ResponseEntity<>(profileService.getPublicProfile(null),HttpStatus.OK);
    }
   
}

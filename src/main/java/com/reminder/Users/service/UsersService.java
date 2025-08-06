package com.reminder.Users.service;

import com.reminder.Users.model.*;
import com.reminder.Users.repository.UsersRepository;
import com.reminder.security.CustomUserDetails;
import com.reminder.Users.utilities.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    public UsersService(UsersRepository usersRepository,
                        JwtUtil jwtUtil,
                        BCryptPasswordEncoder encoder) {
        this.usersRepository = usersRepository;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    final private Pattern validEmail = Pattern.compile("^[a-zA-Z0-9-_~+.]{2,30}@[a-zA-Z0-9]{2,15}(\\.[a-zA-Z]{2,3}){1,2}$");
    final private Pattern validName = Pattern.compile("^(?=.{3,20}$)[a-zA-Z]{2,}(?: [a-zA-Z]{2,})*$");
    final private Pattern validMobile = Pattern.compile("^\\d{10,15}$");
    final private Pattern validUserName = Pattern.compile("^(?!.*( )\1)[a-zA-Z0-9 ._\\-$^~]{5,20}$");
    final private Pattern validPassword = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[-!@#$%^&*()_./]).{8,}$");

    public TokensDTO newUser(UserLogin userCredentials) {
        validateCredentials(userCredentials.getUserName(), userCredentials.getHashedPassword());
        userCredentials.setHashedPassword(passwordHasher(userCredentials.getHashedPassword()));
        userCredentials.setRole("user");
        usersRepository.save(userCredentials);
        TokensDTO response = new TokensDTO(
                jwtUtil.generateJwtToken(userCredentials.getUserName(), userCredentials.getRole()),
                jwtUtil.generateRefreshToken(userCredentials.getUserName(), userCredentials.getRole())
        );
        return response;
    }

    @Transactional
    public void newUserActivation(UserCrm userDetails) {
        validateCrmDetails(userDetails);
        userDetails.setServiceLevel(determineServiceLevel(userDetails.getEmail(), userDetails.getMobile()));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails authUser = (CustomUserDetails) auth.getPrincipal();
        String userName = authUser.getUsername();
        Long id = usersRepository.activate(userDetails);
        usersRepository.updateLoginUserId(userName, id);
    }

    public TokensDTO loginService(UserLogin user) {
        UserLogin savedUser = usersRepository.getUserByUserName(user.getUserName());
        if (!savedUser.isActive())
            throw new InvalidParameterException("");
        if (!encoder.matches(user.getHashedPassword(), savedUser.getHashedPassword()))
            throw new AccessDeniedException("Invalid password");
        return new TokensDTO(jwtUtil.generateJwtToken(savedUser.getUserName(), savedUser.getRole()),
                jwtUtil.generateRefreshToken(savedUser.getUserName(), savedUser.getRole()));
    }

    public void updateMyProfile(Long userId, UserUpdateDTO detailsDTO) {
        validateUpdateDetails(detailsDTO);
        detailsDTO = mergeProfiles(detailsDTO, userId);
        detailsDTO.setServiceLevel(determineServiceLevel(detailsDTO.getEmail(), detailsDTO.getMobile()));
        usersRepository.updateMyProfile(userId, detailsDTO);
    }

    public UserCrm viewMyProfile(Long userId) {
        return usersRepository.getUserProfileById(userId);
    }

    public void resetPassword(PasswordResetDTO password){
        validateCredentials(password.getUserName(), password.getPassword());
        password.setHashedPassword(passwordHasher(password.getPassword()));
        usersRepository.resetPassword(password);
    }

    public void deleteAccount(DeleteAccountDTO dto) {
        String storedHash = usersRepository.getUserByUserName(dto.getUserName()).getHashedPassword();
        if (!encoder.matches(dto.getPassword(),storedHash))
            throw new AccessDeniedException("Account deletion attempted with wrong password: " + dto.getPassword());
        usersRepository.deleteAccount(dto.getId());
    }
    /*
                *****************
                *Utility methods*
                *****************
     */

    private String passwordHasher(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    private void validateCredentials(String userName, String password) {
        System.out.println("Check at validation:"+ userName +";"+ password);
        if (userName == null || userName.isEmpty() || !validUserName.matcher(userName).matches())
            throw new IllegalArgumentException("User name must contain letters, digits single space or ._-$^~");
        if (password == null || password.isEmpty() || !validPassword.matcher(password).matches())
            throw new IllegalArgumentException("Password must be 8-20 characters long and contain at least 1 upper case, 1 lower case, 1 digit and 1 symbol (-!@#$%^&*()_./)");
    }

    private void validateCrmDetails(UserCrm user) {
        if (!validName.matcher(user.getGivenName()).matches())
            throw new IllegalArgumentException("Given name must be 3-20 character length, may include additional name separated by single space and cannot be blank");
        if (!validName.matcher(user.getSurname()).matches())
            throw new IllegalArgumentException("Surname must be 3-20 character length, may include additional name separated by single space and cannot be blank");
        if (!validEmail.matcher(user.getEmail()).matches() && user.getEmail() != null && !user.getEmail().isEmpty())
            throw new IllegalArgumentException("Invalid E-mail address");
        if (!validMobile.matcher(user.getMobile()).matches() && user.getMobile() != null && !user.getMobile().isEmpty())
            throw new IllegalArgumentException("Mobile must be 10-15 digit long, may include state prefix without + or separators");
    }

    private void validateUpdateDetails(UserUpdateDTO details){
        if (details.getEmail() != null && !details.getEmail().isEmpty())
            if (!validEmail.matcher(details.getEmail()).matches())
                throw new IllegalArgumentException("Invalid E-mail address");
        if (details.getMobile() != null && !details.getMobile().isEmpty())
            if (!validMobile.matcher(details.getMobile()).matches())
                throw new IllegalArgumentException("Mobile must be 10-15 digit long, may include state prefix without + or separators");
    }

    private void validateAdminUpdate(AdminEditProfileDTO user) {
        if (user == null)
            throw new IllegalArgumentException("No arguments to update");
        if (user.getGivenName() != null && !user.getGivenName().isEmpty())
            if (!validName.matcher(user.getGivenName()).matches())
                throw new IllegalArgumentException("Given name must be 3-20 character length, may include additional name separated by single space and cannot be blank");
        if (user.getSurname() != null && !user.getSurname().isEmpty())
            if (!validName.matcher(user.getSurname()).matches())
                throw new IllegalArgumentException("Surname must be 3-20 character length, may include additional name separated by single space and cannot be blank");
        if (user.getEmail() != null && !user.getEmail().isEmpty())
            if (!validEmail.matcher(user.getEmail()).matches() && user.getEmail() != null && !user.getEmail().isEmpty())
                throw new IllegalArgumentException("Invalid E-mail address");
        if (user.getMobile() != null && !user.getMobile().isEmpty())
            if (!validMobile.matcher(user.getMobile()).matches() && user.getMobile() != null && !user.getMobile().isEmpty())
                throw new IllegalArgumentException("Mobile must be 10-15 digit long, may include state prefix without + or separators");
    }


    private int determineServiceLevel(String email, String mobile) {
        boolean condition1 = (email != null && !email.isEmpty());
        boolean condition2 = (mobile != null && !mobile.isEmpty());
        if (condition1 && condition2)
            return 3;
        if (condition1 || condition2)
            return 2;
        else return 1;
    }

    private UserUpdateDTO mergeProfiles (UserUpdateDTO newProfile, long id){
        UserCrm existingProfile = usersRepository.getUserProfileById(id);
        if (newProfile.getEmail() == null || newProfile.getEmail().isEmpty())
            newProfile.setEmail(existingProfile.getEmail());
        if (newProfile.getMobile() == null || newProfile.getMobile().isEmpty())
            newProfile.setMobile(existingProfile.getMobile());
        return newProfile;
    }



    public List<UserCrm> getAllProfiles() {
        return usersRepository.getAllProfiles();
    }

    public UserCrm getProfileById(Long id) {
        return usersRepository.adminGetProfileById(id);
    }

    public List<UserCrm> searchProfile(SearchProfileDTO search) {
        return usersRepository.searchProfile(search);
    }

    public void updateUserProfile(AdminEditProfileDTO userProfile, Long userId) {
        validateAdminUpdate(userProfile);
        StringBuilder sqlBuilder = new StringBuilder("UPDATE user_crm SET");
        List<Object> params = new ArrayList<>();
        boolean first = true;
        if (userProfile.getGivenName() != null && !userProfile.getGivenName().isEmpty()){
            sqlBuilder.append(" given_name = ?");
            params.add(userProfile.getGivenName());
            first = false;
        }
        if (userProfile.getSurname() != null && !userProfile.getSurname().isEmpty()){
            if (!first)
                sqlBuilder.append(", ");
            sqlBuilder.append(" surname = ?");
            params.add(userProfile.getSurname());
            first = false;
        }
        if (userProfile.getEmail() != null && !userProfile.getEmail().isEmpty()){
            if (!first)
                sqlBuilder.append(", ");
            sqlBuilder.append(" email_address = ?");
            params.add(userProfile.getEmail());
            first = false;
        }
        if (userProfile.getMobile() != null && !userProfile.getMobile().isEmpty()){
            if (!first)
                sqlBuilder.append(", ");
            sqlBuilder.append(" mobile = ?");
            params.add(userProfile.getMobile());
            first = false;
        }
        if (userProfile.getServiceLevel() != 0){
            if (!first)
                sqlBuilder.append(", ");
            sqlBuilder.append(" service_level = ?");
            params.add(userProfile.getServiceLevel());
            first = false;
        }
        if (first)
            throw new IllegalArgumentException ("No arguments to update");
        sqlBuilder.append(" WHERE id = ?");
        params.add(userId);
        String sql = sqlBuilder.toString();
        usersRepository.updateProfile(sql, params);
    }

    public String setActiveStatus(Long id, boolean isActive) {
        String wanted = isActive ? "activated" : "deactivated";
        boolean currentStatus = usersRepository.checkStatus(id);
        if ((isActive && currentStatus) || (!isActive && !currentStatus))
            throw new IllegalArgumentException(String.format("User %s already %s.", id, wanted));
        return String.format("User %s successfully %s", id, wanted);
    }

    public Long newSpecialUser(UserLogin user) {
        return usersRepository.saveSpecial(user);
    }
}
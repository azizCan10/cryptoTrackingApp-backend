package com.test.cryptotrackapp.service;

import com.test.cryptotrackapp.core.result.DataResult;
import com.test.cryptotrackapp.core.result.Result;
import com.test.cryptotrackapp.core.result.SuccessDataResult;
import com.test.cryptotrackapp.core.result.SuccessResult;
import com.test.cryptotrackapp.dto.converter.UserConverter;
import com.test.cryptotrackapp.dto.user.CreateUserRequest;
import com.test.cryptotrackapp.dto.user.UserDto;
import com.test.cryptotrackapp.dto.userCryptoList.UserCryptoListDto;
import com.test.cryptotrackapp.entity.User;
import com.test.cryptotrackapp.exception.NotFoundException;
import com.test.cryptotrackapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter converter;
    private final UserCryptoListService userCryptoListService;

    public Result add(CreateUserRequest user) {
        userRepository.save(converter.convert(user));
        return new SuccessResult("Account created. Happy tracking!");
    }

    public DataResult<UserDto> getById(long id) {
        UserDto userDto = converter.convert(findUserById(id));

        List<String> symbols = userCryptoListService.findByUserId(id).stream()
                .map(UserCryptoListDto::getSymbol)
                .collect(Collectors.toList());

        userDto.setTrackedCryptos(symbols);

        return new SuccessDataResult<>(userDto, "User listed.");
    }

    public Result delete(long id) {
        findUserById(id);

        userRepository.deleteById(id);
        return new SuccessResult("Account removed.");
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User couldn't find by id: " + id));
    }
}
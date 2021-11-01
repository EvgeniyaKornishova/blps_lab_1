package ru.itmo.blps_lab_1.data.dto;

import lombok.Data;
import ru.itmo.blps_lab_1.data.User;
import ru.itmo.blps_lab_1.data.Watchlist;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDto {
    private String username;

    private Set<Long> watchlists;

    public static UserDto fromUser(User user){
       UserDto userDto = new UserDto();

       userDto.setUsername(user.getUsername());
       userDto.setWatchlists(
              user.getWatchlists().stream().map(Watchlist::getId).collect(Collectors.toSet())
       );

       return userDto;
    }

}

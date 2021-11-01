package ru.itmo.blps_lab_1.data.dto;

import lombok.Data;
import ru.itmo.blps_lab_1.data.Equity;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class EquityDto {
    private Long id;

    private String name;
    private String symbol; // Short uniq equity name

    private Float last;
    private Float prev;

    // change = last-prev
    // change% = change / prev * 100

    private Float high;
    private Float low;

    private Float open;

    private Float bid;
    private Float ask;

    private Float extendedHours;
    // extended hours in percents

    private Date nextEarningDate;
    private Time time; // 0 if closed

    private Float vol;

    public static EquityDto fromEquity(Equity equity){
        EquityDto equityDto = new EquityDto();

        equityDto.setAsk(equity.getAsk());
        equityDto.setBid(equity.getBid());
        equityDto.setExtendedHours(equity.getExtendedHours());
        equityDto.setHigh(equity.getHigh());
        equityDto.setId(equity.getId());
        equityDto.setLast(equity.getLast());
        equityDto.setLow(equity.getLow());
        equityDto.setName(equity.getName());
        equityDto.setOpen(equity.getOpen());
        equityDto.setNextEarningDate(equity.getNextEarningDate());
        equityDto.setPrev(equity.getPrev());
        equityDto.setSymbol(equity.getSymbol());
        equityDto.setTime(equity.getTime());
        equityDto.setVol(equity.getVol());

        return equityDto;
    }

    public static List<EquityDto> fromEquitiesList(List<Equity> equities){
        return equities.stream().map(EquityDto::fromEquity).collect(Collectors.toList());
    }
}

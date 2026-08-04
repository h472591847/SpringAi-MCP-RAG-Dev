package com.itzixi.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName ListSortEnum
 * @Description
 * @Author oliver
 * @Date 2026/8/3 15:54
 */
@AllArgsConstructor
@Getter
public enum ListSortEnum {
    ASC("asc", "正序"),
    DESC("desc", "倒序");

    private final String type;
    private final String value;


    public static ListSortEnum getTypeByValue(String sort) {
        for (ListSortEnum value : ListSortEnum.values()) {
            if (value.getValue().equals(sort)) {
                return value;
            }
        }
        return null;
    }
}

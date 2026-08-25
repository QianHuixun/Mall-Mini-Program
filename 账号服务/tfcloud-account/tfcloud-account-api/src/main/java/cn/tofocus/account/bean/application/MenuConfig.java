package cn.tofocus.account.bean.application;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuConfig
{
    private String owner;
    
    private String application;
    
    private String model;
    
    private List<String> menus;
}

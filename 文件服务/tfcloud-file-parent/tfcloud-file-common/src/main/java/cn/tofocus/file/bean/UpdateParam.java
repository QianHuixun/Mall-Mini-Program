package cn.tofocus.file.bean;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateParam
{
    @NotBlank
    private String file;
    
    @NotBlank
    @Size(max = 40)
    private String domain;
    
    @NotBlank
    @Size(max = 100)
    private String db;
    
    @NotBlank
    @Size(max = 100)
    private String table;
    
    @NotBlank
    @Size(max = 100)
    private String pkey;
    
    @NotNull
    private UploadType upType;
    
    @Size(max = 40)
    private String org;
    
    @Size(max = 40)
    private String dept;
}

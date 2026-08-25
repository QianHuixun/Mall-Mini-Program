package cn.tofocus.account.bean.application;

import java.util.Set;

import cn.tofocus.core.data.StrKeyName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DeptModelKv extends StrKeyName
{
    private Set<String> models;
}

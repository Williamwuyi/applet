package com.jiebao.platfrom.common.dataImport.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("hlb_info")
public class Info extends Model<Info> implements Serializable {

    private static final long serialVersionUID = -5302699040910015644L;

    @Override
    protected Serializable pkVal() {
        return ID;
    }

    @TableId(value = "ID", type = IdType.UUID)
    private String ID;

    private String Title;

    private String Content;

    private Integer Counter;

    private String upTime;

    private String upUser;

    private String infoSeq;

    private String info_class;

    private String info_type;

    private String upFile1;

    private String upFile2;

    private String upFile3;

    private String upFile4;

    private String sendTouser;

    private Integer commitcounter;

    private String deleteuser;

}

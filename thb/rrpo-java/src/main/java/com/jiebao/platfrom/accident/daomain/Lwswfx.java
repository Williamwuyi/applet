package com.jiebao.platfrom.accident.daomain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author qta
 * @since 2020-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Lwswfx implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    private String tjyf;

    private String sbgac;

    private String fssj;

    private String fsdsz;

    private String fsxsdq;

    private String pcs;

    private String xl;

    private String znqj;

    private String fsdd;

    private String cwd;

    private String gwd;

    private String sgxz;

    private String hlms;

    private String fbcd;

    private String xb;

    private String nld;

    private String sf;

    private String jzd;

    private String ltlyj;

    private String sgqx;

    private String dnfxxs;

    private String gafxxs;

    private String hlbfxxs;

    private String rscd;

    private String xszr;

    private String csld;

    private String zgzrxs;

    private String hssw;

    private String mainKey;

    private String wzflag;


}

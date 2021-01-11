package com.jiebao.platfrom.accident.daomain;

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
 * @since 2020-12-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Zaajqk implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer 编号;

    private String tjyf;

    private String sbgac;

    private String fssj;

    private String fsdsz;

    private String fsxsdq;

    private String fsxzc;

    private String pcs;

    private String xl;

    private String fsdd;

    private String ajmc;

    private String tcsj;

    private String fbzt;

    private String ajqk;

    private String ajzt;

    private String zrsm;

    private String bz;

    private String mainKey;

    private String wzflag;


}

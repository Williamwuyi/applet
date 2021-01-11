package com.jiebao.platfrom.accident.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-11-15
 */
@RestController

@RequestMapping("/accident/lwswfx")
//@Api(tags = "accident-事故分析")
public class LwswfxController {
//    @Autowired
//    ILwswfxService lwswfxService;
//    @Autowired
//    DeptMapper deptMapper;
//
//    @Autowired
//    DictMapper dictMapper;
//
//    @Autowired
//    IAccidentService accidentService;
//
//    @GetMapping("list")
//    @ApiOperation("事故分析")
//    public void list() {
//        List<String> shiList = new ArrayList<>();
//        List<String> xianList = new ArrayList<>();
//        List<String> suoList = new ArrayList<>();
//        List<String> cheList = new ArrayList<>();
//        List<String> gongList = new ArrayList<>();
//        QueryWrapper<Lwswfx> queryWrapper = new QueryWrapper<>();
//        queryWrapper.groupBy("fsxsdq");
//        for (Lwswfx lwswfx : lwswfxService.list(queryWrapper)) {
//            //    System.out.println(lwswfx);
////            if(lwswfx.getFsdsz()!=null)
////            if (deptMapper.getByNewName(lwswfx.getFsdsz()) == null) {
////                shiList.add(lwswfx.getFsdsz());
////            }
//            try {
//                if (lwswfx.getFsxsdq() != null)
//                    if (deptMapper.getByNewName(lwswfx.getFsxsdq()) == null) {
//                        xianList.add(lwswfx.getFsxsdq());
//                    }
//            } catch (MyBatisSystemException e) {
//                shiList.add(lwswfx.getFsdsz());
//                xianList.add(lwswfx.getFsxsdq());
//            }
//
////            if(lwswfx.getPcs()!=null)
////            if (deptMapper.getByNewName(lwswfx.getPcs()) == null) {
////               suoList.add(lwswfx.getPcs());
////            }
////            if(lwswfx.getCwd()!=null)
////            if (deptMapper.getByNewName(lwswfx.getCwd()) == null) {
////                cheList.add(lwswfx.getCwd());
////            }
////            if(lwswfx.getGwd()!=null)
////            if (deptMapper.getByNewName(lwswfx.getGwd()) == null) {
////                gongList.add(lwswfx.getGwd());
////            }
////
////            if(lwswfx.getXl()!=null)
////                if (dictMapper.getByNewNames(lwswfx.getXl()) == null) {
////                    gongList.add(lwswfx.getXl());
////                }
//        }
//
//        for (String o : shiList) {
//            System.out.println(o);
//        }
//        System.out.println("-----------");
//        System.out.println("--------------");
//        for (String o : xianList) {
//            System.out.println(o);
//        }
//        System.out.println("-----------");
//        System.out.println("--------------");
//        for (String o : suoList) {
//            System.out.println(o);
//        }
//        System.out.println("-----------");
//        System.out.println("--------------");
//        for (String o : cheList) {
//            System.out.println(o);
//        }
//        System.out.println("-----------");
//        System.out.println("--------------");
//        for (String o : gongList) {
//            System.out.println(o);
//        }
//        System.out.println("-----------");
//        System.out.println("--------------");
//    }
//
//    @GetMapping("addList")
//    @ApiOperation("导入数据")
//    @Transactional(rollbackFor = Exception.class)
//    public void ad() {
//        List<Accident> list = new ArrayList<>();
//        for (Lwswfx lwswfx : lwswfxService.list()) {
//            if (lwswfx.getFssj() == null) {
//                continue;
//            }
//            Accident accident = new Accident();
//            accident.setMonth(lwswfx.getTjyf());
//            accident.setPoliceFather(deptMapper.getIdByName(lwswfx.getSbgac()));
//            accident.setDate(lwswfx.getFssj());
//            if (lwswfx.getFsxsdq().equals("高新区")) {
//                accident.setCityCsId("1221");
//                accident.setCityQxId("1310");
//            } else {
//                accident.setCityCsId(deptMapper.getIdByName(lwswfx.getFsdsz()));
//                accident.setCityQxId(deptMapper.getIdByName(lwswfx.getFsxsdq()));
//            }
//            accident.setPoliceId(deptMapper.getIdByName(lwswfx.getPcs()));
//            accident.setLineId(dictMapper.getIdByName(lwswfx.getXl()));
//            accident.setTrainId(dictMapper.getIdByName(lwswfx.getCwd()));
//            accident.setTrackId(dictMapper.getIdByName(lwswfx.getGwd()));
//            accident.setNature(lwswfx.getSgxz());
//            accident.setInstationSection(lwswfx.getZnqj());
//            accident.setRoad(lwswfx.getHlms());
//            accident.setAge(lwswfx.getNld());
//            accident.setClosed(lwswfx.getFbcd());
//            accident.setJzd(lwswfx.getJzd());
//            accident.setDistance(lwswfx.getLtlyj());
//            accident.setIdentity(lwswfx.getSf());
//            accident.setConditions(lwswfx.getSgqx());
//            switch (lwswfx.getDnfxxs()) {
//                case "A":
//                    accident.setDnxs(0.1);
//                    break;
//                case "B":
//                    accident.setDnxs(0.2);
//                    break;
//                case "C":
//                    accident.setDnxs(0.3);
//                    break;
//                case "D":
//                    accident.setDnxs(0.4);
//                    break;
//                case "E":
//                    accident.setDnxs(0.5);
//                    break;
//                case "F":
//                    accident.setDnxs(0.6);
//                    break;
//                case "G":
//                    accident.setDnxs(0.7);
//                    break;
//                case "H":
//                    accident.setDnxs(0.8);
//                    break;
//                case "I":
//                    accident.setDnxs(0.9);
//                    break;
//                case "J":
//                    accident.setDnxs(1.0);
//                    break;
//            }
//            DecimalFormat decimalFormat = new DecimalFormat("0.0000");
//            try {
//                accident.setDntjxs((Double) decimalFormat.parse(decimalFormat.format(accident.getDnxs() - 0.05)));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            try {
//                System.out.println(lwswfx.getHssw());
//                accident.setGaxs(Double.parseDouble(decimalFormat.format(Double.parseDouble(lwswfx.getHssw()))));
//            } catch (NumberFormatException e) {
//                continue;
//            } catch (NullPointerException e) {
//                continue;
//            }
//            accident.setDeathToll(Double.parseDouble(decimalFormat.format(Double.parseDouble(lwswfx.getHssw()))));
//            accident.setStatu(1);
//            accident.setSex(dictMapper.getIdByName(lwswfx.getXb()));
//            list.add(accident);
//        }
//
//        for (Accident accident : list) {
//            System.out.println(accident);
//        }
//        accidentService.saveBatch(list);
//
//    }

    

}

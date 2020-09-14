export default {
  // 构建部门树
  transData: function (a, idStr, pidStr, chindrenStr) {
    let r = [],
      hash = {},
      id = idStr,
      pid = pidStr,
      children = chindrenStr,
      i = 0,
      j = 0,
      len = a.length;
    for (; i < len; i++) {
      hash[a[i][id]] = a[i];
    }
    for (; j < len; j++) {
      var aVal = a[j],
        hashVP = hash[aVal[pid]];
      if (hashVP) {
        !hashVP[children] && (hashVP[children] = []);
        hashVP[children].push(aVal);
      } else {
        r.push(aVal);
      }
    }
    return r;
  },
  // 构造出有权限的部门树
  designTree: function (arr, allArr, newArr) {
    if (arr == null || arr == '') {
      console.log("arr为空");
      return [];
    } else {
      for (let i = 0; i < arr.length; i++) {
        let a = arr[i];
        newArr.push(a);
        if (a["parentId"] == 0) {
          continue;
        }
        let s = allArr.filter(item => item["deptId"] == a["parentId"]);
        this.xxx(newArr, allArr, s);
      }
      return newArr;
    }
  },
  // 递归函数
  xxx: function (newArr, allArr, s) {
    if (newArr.filter(item => item["deptId"] == s[0]["deptId"]).length != 0) {
      return;
    }
    newArr.push(s[0]);
    if (s[0]["parentId"] == 0) {
      return;
    } else {
      let d = allArr.filter(item => item["deptId"] == s[0]["parentId"]);
      xxx(newArr, allArr, d);
    }
  },
  // 重构报表，添加会员人数、非会员
  reconsitution: function (data) {
    let r = [];
    for (let n of data) {
      r.push({
        ...n,
        l_notMember: Number(n.allCount) - Number(n.paymentCount) - Number(n.generalCount),//非会员
        l_isMember: Number(n.paymentCount) + Number(n.generalCount)//会员
      })
    }
    return r;
  },
  // 过滤数据，累加各数据的年龄段
  countAges: function (data) {
    let age1, age2, age3, age4, age5;
    age1 = age2 = age3 = age4 = age5 = 0;
    for (let n of data) {
      age1 += n.age1Count - 0;
      age2 += n.age2Count - 0;
      age3 += n.age3Count - 0;
      age4 += n.age4Count - 0;
      age5 += n.age5Count - 0;
    }
    return { age1Count: age1, age2Count: age2, age3Count: age3, age4Count: age4, age5Count: age5 };
  },
  // 过滤数据，累加各数据的年龄段
  countMembers: function (data) {
    let isM, notM;
    isM = notM = 0;
    for (let n of data) {
      isM += n.l_isMember - 0;
      notM += n.l_notMember - 0;
    }
    return { l_isMember: isM, l_notMember: notM };
  }
};

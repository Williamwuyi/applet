export default {
    //点击表格行变色
    click: function(event) {
        let c = event.target.parentNode;
        let len = c.parentNode.children.length;
        for (let i = 0; i < len; i++) {
            c.parentNode.children[i].classList.remove("ant-table-row-selected");
        }
        c.classList.add("ant-table-row-selected");
    }
};

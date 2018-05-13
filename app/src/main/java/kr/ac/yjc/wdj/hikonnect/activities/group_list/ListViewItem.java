package kr.ac.yjc.wdj.hikonnect.activities.group_list;

import android.content.Context;

/**
 * @author  Jiyoon Lee
 * @since   2018-04-10
 */
public class ListViewItem {

    private String head;
    private Context parent;
//    private String desc;
//    private String imageUrl;

    public ListViewItem(String head, Context parent){
        this.head=head;
        this.parent = parent;
//        this.desc=desc;
//        this.imageUrl=imageUrl;
    }
    public String getHead() { return head; }
//    public String getDesc() { return desc; }
//    public String getImageUrl() {
//        return imageUrl;
//    }

    public Context getParent() {
        return this.parent;
    }
}
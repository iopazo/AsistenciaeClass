package com.moveapps.asistenciaeclass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iopazog on 14-09-14.
 */
public class Group {

    public String string;
    public final List<String> children = new ArrayList<String>();

    public Group(String string) {
        this.string = string;
    }

}

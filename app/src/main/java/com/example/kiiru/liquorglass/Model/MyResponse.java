package com.example.kiiru.liquorglass.Model;

import java.util.List;

/**
 * Created by Kiiru on 11/13/2017.
 */

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}

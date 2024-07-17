package com.bruce.dufs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @date 2024/7/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMeta {

    private String name;
    private String originalFilename;
    private long size;
    private Map<String, String> tags = new HashMap<>();
}

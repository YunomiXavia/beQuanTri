package com.example.beQuanTri.dto.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Lớp chứa thông tin về tên và đường dẫn của các ảnh sản phẩm.
 */
@Getter
@Setter
@AllArgsConstructor
public class ImageInfo {
    private String originalName;
    private String smallName;
    private String tinyName;
    private String originalUrl;
    private String smallUrl;
    private String tinyUrl;
}

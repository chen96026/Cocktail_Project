package com.example.cocktail.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * @param file 要上傳的圖片
     * @return 上傳後的 https 圖片 URL
     * @throws IOException 讀取檔案或上傳失敗
     */
    public String uploadImage(MultipartFile file) throws IOException {
        // Cloudinary SDK 回傳的是沒有泛型的 Map，值都是 Object，secure_url 實際上是 String
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }
}

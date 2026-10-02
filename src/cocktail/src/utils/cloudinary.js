// Cloudinary 圖片縮圖：在網址的 /upload/ 後面插入轉換參數，讓 CDN 依顯示大小回傳壓縮過的圖

// 只處理 Cloudinary 的圖片上傳網址：https://res.cloudinary.com/<cloud>/image/upload/<其餘路徑>
const CLOUDINARY_UPLOAD_URL = /^(https?:\/\/res\.cloudinary\.com\/[^/]+\/image\/upload\/)(.+)$/;

// 轉換參數段落的開頭，例如 c_limit,w_640 或 t_thumb；版本號 v123 沒有底線所以不會被當成轉換參數
const TRANSFORMATION_SEGMENT = /^\$?[a-z]{1,3}_/;

/**
 * 各顯示位置要的圖片寬度（px）
 * 以 1440 寬螢幕、2 倍像素密度估算，對應 App.css 裡的圖片寬度
 */
export const IMAGE_WIDTH = {
    card: 640, // 調酒列表卡片 #cocktaillistImg（22vw）
    modal: 520, // 詳細介紹彈窗 .modalSmallDiv img（18vw）
    result: 720, // 篩選器結果 #printImg（25vw）
};

/**
 * @param url   原始圖片網址
 * @param width 顯示需要的最大寬度（px），原圖比這個小時不放大
 * @returns 帶轉換參數的網址；非 Cloudinary 網址、或已經帶轉換參數的網址原樣回傳
 */
export const toCloudinaryThumbnail = (url, width) => {
    const match = typeof url === "string" ? url.match(CLOUDINARY_UPLOAD_URL) : null;
    if (!match) {
        return url;
    }
    const [, prefix, rest] = match;
    const segments = rest.split("/");
    // 轉換參數後面一定還接著檔名，所以只有不是最後一段時才可能是轉換參數
    if (segments.length > 1 && TRANSFORMATION_SEGMENT.test(segments[0])) {
        return url;
    }
    return `${prefix}c_limit,w_${width},f_auto,q_auto/${rest}`;
};

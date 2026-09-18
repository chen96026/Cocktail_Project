import {useEffect, useState} from "react";

//從父元件拿到image
const Carousel = ({images}) => {
    //Carousel 用「首尾各多一張」做無縫輪播，所以真正的第一張圖放在索引 1，useState初始值給1
    const [currentIndex, setCurrentIndex] = useState(1);
    //記錄輪播動畫是否正在進行，false 時不套用 transition，用來做「瞬移」
    const [isAnimating, setIsAnimating] = useState(false);

    //變成["圖4", "圖1", "圖2", "圖3", "圖4", "圖1"]的陣列
    const extendedImages = [
        // 取最後一張（圖4）放到最前面
        images[images.length - 1],
        // 原本的圖1、圖2、圖3、圖4
        ...images,
        // 取第一張（圖1）放到最後面
        images[0]
    ];

    const goTo = (index) => {
        setIsAnimating(true);
        setCurrentIndex(index);
    };

    //6秒換一次
    useEffect(() => {
        const interval = setInterval(() => {
            // 使用函式型更新，避免吃到舊的 currentIndex
            setIsAnimating(true);
            setCurrentIndex((prevIndex) => prevIndex + 1);
        }, 6000);
        //確保舊的定時器不會繼續跑，避免記憶體洩漏與重複執行
        return () => clearInterval(interval);
    }, []);

    //動畫播完才處理無縫接回，不必跟 CSS 的 0.5s 對時間
    const handleTransitionEnd = () => {
        setIsAnimating(false);
        //現在停在最後面那張假的圖1，關掉動畫後瞬移回真正的第一張
        if (currentIndex === images.length + 1) {
            setCurrentIndex(1);
        }
    };

    return (
        <section id="main_background-img">
            <div id="carousel-container"
                onTransitionEnd={handleTransitionEnd}
                style={{
                    // currentIndex * 100% 表示每切換一張圖片，整個容器往左移一張圖片的寬度，- 號表示往左移動
                    transform: `translateX(-${currentIndex * 100}%)`,
                    // 動畫中才套用位移動畫；瞬移首尾圖片時關掉，避免閃爍
                    transition: isAnimating ? "transform 0.5s ease-in-out" : "none",}}>
                {/*extendedImages 是已經在首尾各多加一張的圖片陣列*/}
                {extendedImages.map((image, index) => (
                    <div className="carousel-item" key={index}>
                        <img src={image} alt={`Slide ${index}`}/>
                    </div>))}
            </div>
            {/*.map() 用來根據每張圖片產生一個圓點，第一個參數 _ 是圖片本身（因為沒用到內容，所以用 _ 代表忽略*/}
            <div id="carousel-indicators">
                {images.map((_, index) => (
                    // 每個圓點都有 indicator 樣式，如果 index === currentIndex - 1，就加上 active 樣式，要減 1 才能對應到原始圖片陣列的 index
                    <span
                        key={index}
                        className={`indicator ${index === currentIndex - 1 ? "active" : ""}`}
                        // 點擊某個圓點時，把 currentIndex 設成 index + 1，extendedImages 的第一張真圖在位置 1（位置 0 是假圖）
                        onClick={() => goTo(index + 1)}>
                    </span>))}
            </div>
        </section>
    );
};

export default Carousel;

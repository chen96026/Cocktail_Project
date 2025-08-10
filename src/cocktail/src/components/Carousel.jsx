import React, {useState, useEffect} from "react";

//從父元件拿到image
const Carousel = ({images}) => {
    //currentIndex：讀取目前圖片的索引值、setCurrentIndex：改變 currentIndex 的函數，
    //Carousel 有用「首尾各多一張」的技巧（無縫輪播），所以真正的第一張圖放在索引 1，useState初始值給1
    const [currentIndex, setCurrentIndex] = useState(1);
    //記錄輪播動畫是否正在進行
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

    //下一張輪播圖
    const handleNext = () => {
        // 防護機制，如果還在動畫中就return，不執行下面的換圖
        if (isAnimating) return;
        // 標記「動畫開始」
        setIsAnimating(true);
        // 切換到下一張，這邊使用函式型更新，因為 setState 在 React 是非同步的，有時候你連續呼叫多次 setState，如果直接用變數加減，可能會吃到舊資料
        setCurrentIndex((prevIndex) => prevIndex + 1);
    };

    //副的作用開始，處理渲染之外的事，只要 isAnimating 改變，就會執行這段程式
    useEffect(() => {
        //6秒換一次
        const interval = setInterval(() => {
            //如果現在不是動畫中，就執行 handleNext() 切到下一張
            if (!isAnimating) handleNext();
        }, 6000);
        //確保舊的定時器不會繼續跑，避免：記憶體洩漏、重複執行 handleNext
        return () => clearInterval(interval);
        //isAnimating 變了 → 清掉舊的定時器 → 建立新的定時器
    }, [isAnimating]);

    //第二個副的作用，處理無縫過渡邏輯，只要 currentIndex 改變，就會執行這段程式
    useEffect(() => {
        //如果是索引0，代表是假的圖4，代表使用者往左滑，此時先關閉動畫，然後瞬移到真正的最後一張
        if (currentIndex === 0) {
            setTimeout(() => {
                setIsAnimating(false);
                setCurrentIndex(images.length);
            }, 500);
            //如果是最後一張的下一張，代表是假的圖1，代表使用者往右滑，此時先關閉動畫，然後瞬移到真正的第一張
        } else if (currentIndex === images.length + 1) {
            setTimeout(() => {
                setIsAnimating(false);
                setCurrentIndex(1);
            }, 500);
        } else {
            setTimeout(() => setIsAnimating(false), 500);
        }
    }, [currentIndex]);

    return (
        <section id="main_background-img">
            <div id="carousel-container"
                style={{
                    // currentIndex * 100% 表示每切換一張圖片，整個容器往左移一張圖片的寬度，- 號表示往左移動
                    transform: `translateX(-${currentIndex * 100}%)`,
                    // 當 isAnimating 為 true 時，使用平滑的 0.5 秒位移動畫，當 isAnimating 為 false 時，不做動畫（用在「瞬移」首尾圖片時避免閃爍）
                    // 1.是否在動畫中 2.動畫中:transition: [要變化的屬性] [時間] [速度曲線] 3.非動畫中:瞬間完成位置變化
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
                        onClick={() => setCurrentIndex(index + 1)}>
                    </span>))}
            </div>
        </section>
    );
};

export default Carousel;

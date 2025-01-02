import React, {useState, useEffect} from "react";

const Carousel = ({images}) => {
    const [currentIndex, setCurrentIndex] = useState(1);
    const [isAnimating, setIsAnimating] = useState(false);

    const extendedImages = [
        images[images.length - 1], //從原始陣列中取最後一張圖片，放在頭部
        ...images,
        images[0]//從原始陣列中取第一張圖片，放在尾部
    ];

    const handleNext = () => {
        if (isAnimating) return;
        setIsAnimating(true);
        setCurrentIndex((prevIndex) => prevIndex + 1);
    };

    useEffect(() => {
        const interval = setInterval(() => {
            if (!isAnimating) handleNext();
        }, 6000);

        return () => clearInterval(interval);
    }, [isAnimating]);

    //處理無縫過渡邏輯
    useEffect(() => {
        if (currentIndex === 0) {
            setTimeout(() => {
                setIsAnimating(false);
                setCurrentIndex(images.length);
            }, 500);
        } else if (currentIndex === images.length + 1) {
            setTimeout(() => {
                setIsAnimating(false);
                setCurrentIndex(1);//將 currentIndex 重置為 1，即真正的第一張圖片，完成無縫過渡
            }, 500);
        } else {
            setTimeout(() => setIsAnimating(false), 500);
        }
    }, [currentIndex]);

    return (
        <section id="main_background-img">
            <div
                id="carousel-container"
                style={{
                    transform: `translateX(-${currentIndex * 100}%)`,
                    transition: isAnimating ? "transform 0.5s ease-in-out" : "none",
                }}
            >
                {extendedImages.map((image, index) => (
                    <div className="carousel-item" key={index}>
                        <img src={image} alt={`Slide ${index}`}/>
                    </div>
                ))}
            </div>
            <div id="carousel-indicators">
                {images.map((_, index) => (
                    <span
                        key={index}
                        className={`indicator ${index === currentIndex - 1 ? "active" : ""}`}
                        onClick={() => setCurrentIndex(index + 1)}
                    ></span>
                ))}
            </div>
        </section>
    );
};

export default Carousel;

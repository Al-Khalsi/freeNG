import { useEffect, useState } from "react";
import { TbPng } from "react-icons/tb";

const Footer = () => {
    const [iconCount, setIconCount] = useState(0);

    const calculateIconCount = () => {
        const width = window.innerWidth;
        const iconWidth = 45;
        const count = Math.floor(width / iconWidth);
        setIconCount(count);
    };

    useEffect(() => {
        calculateIconCount();
        window.addEventListener("resize", calculateIconCount);
        return () => {
            window.removeEventListener("resize", calculateIconCount);
        };
    }, []);

    return (
        <footer className="footer w-full h-48">
            <section className="relative flex flex-col w-full h-full bg-bgDarkGray">
                <div className="flex flex-nowrap w-full text-clDarkGray2">
                    {Array.from({ length: iconCount }, (_, index) => (
                        <TbPng key={index} className="text-5xl -my-3 flex-grow" /> // تنظیم اندازه آیکون
                    ))}
                </div>
                
            </section>
        </footer>
    );
};

export default Footer;
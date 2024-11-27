import { useEffect, useState } from "react";
import { TbPng } from "react-icons/tb";

const Footer = () => {
    const [iconCount, setIconCount] = useState(0);

    // تابعی برای محاسبه تعداد آیکون‌ها بر اساس عرض صفحه
    const calculateIconCount = () => {
        const width = window.innerWidth; // استفاده از عرض کل صفحه
        const iconWidth = 50; // عرض تقریبی هر آیکون (می‌توانید این مقدار را تغییر دهید)
        const count = Math.floor(width / iconWidth); // محاسبه تعداد آیکون‌ها
        setIconCount(count);
    };

    useEffect(() => {
        calculateIconCount(); // محاسبه اولیه
        window.addEventListener("resize", calculateIconCount); // اضافه کردن لیسنر برای تغییر اندازه
        return () => {
            window.removeEventListener("resize", calculateIconCount); // پاک کردن لیسنر
        };
    }, []);

    return (
        <footer className="footer w-full h-48">
            <section className="relative flex flex-col w-full h-full bg-bgDarkGray">
                <div className="flex flex-nowrap w-full text-4xl">
                    {Array.from({ length: iconCount }, (_, index) => (
                        <TbPng key={index} className="w-[50px] h-[50px] -my-2 flex-grow" /> // تنظیم اندازه آیکون
                    ))}
                </div>
            </section>
        </footer>
    );
};

export default Footer;
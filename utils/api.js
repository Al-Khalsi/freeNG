export const apiFetch = async (url, method = 'GET', body = null, customOptions = {}) => {
    const options = {
        method,
        ...customOptions, // اضافه کردن گزینه‌های سفارشی
    };

    if (body) {
        if (body instanceof FormData) {
            options.body = body; // اگر body از نوع FormData باشد، مستقیماً ارسال می‌شود
        } else {
            options.body = JSON.stringify(body);
        }
    }

    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return await response.json();
    } catch (error) {
        console.error('Fetch error:', error);
        throw error; // دوباره پرتاب کردن خطا برای مدیریت در تابع فراخوانی
    }
};
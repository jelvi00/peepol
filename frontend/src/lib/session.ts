
export const setStorageItem = (key: string, value: unknown) => {

    sessionStorage.setItem(key, JSON.stringify(value));

};

export const getStorageItem = (key: string) => {

    const item = sessionStorage.getItem(key);

    return item ? JSON.parse(item) : undefined;

}

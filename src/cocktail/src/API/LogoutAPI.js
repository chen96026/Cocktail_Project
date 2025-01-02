export const logoutMember = async (account) => {
    try {
        const response = await fetch(`/lastwine/logout`, {
            method: "POST",
            credentials: "include",
        });
        if (!response.ok) {
            throw new Error(`HTTP error status: ${response.status}`);
        }
        return await response.json();
        return data;
    } catch (error) {
        console.error("錯誤: ", error);
        throw error;
    }
}
export const logoutMember = async () => {
    try {
        const response = await fetch(`/lastwine/logout`, {
            method: "POST",
            credentials: "include",
        });
        if (!response.ok) {
            throw new Error(`HTTP error status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error("錯誤: ", error);
        throw error;
    }
}
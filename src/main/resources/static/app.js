const API_BASE = "/api";

function getAccessToken() {
    return localStorage.getItem("accessToken");
}

function getRefreshToken() {
    return localStorage.getItem("refreshToken");
}

function setTokens(access, refresh) {
    localStorage.setItem("accessToken", access);
    localStorage.setItem("refreshToken", refresh);
}

async function apiRequest(url, options = {}) {

    const headers = options.headers || {};
    headers["Content-Type"] = "application/json";

    const token = getAccessToken();
    if (token) headers["Authorization"] = "Bearer " + token;

    const response = await fetch(API_BASE + url, {
        ...options,
        headers
    });

    if (response.status === 401) {
        const refreshed = await refreshTokenFlow();
        if (refreshed) return apiRequest(url, options);
        window.location = "login.html";
        return;
    }

    let data = null;

        // Safely parse JSON (in case of 204 or empty body)
        try {
            data = await response.json();
        } catch (e) {
            data = null;
        }

        // If HTTP status is error, handle it
        if (!response.ok) {

            const message =
                data?.error?.message ||
                data?.message ||
                "Request failed";

            alert(message);
            throw new Error(message);
        }

        // If backend uses wrapped response pattern
        if (data && typeof data === "object" && "success" in data) {

            if (!data.success) {
                const message = data?.error?.message || "Operation failed";
                alert(message);
                throw new Error(message);
            }

            return data.data;
        }

        // Otherwise return raw JSON (arrays, objects, etc.)
        return data;
}

async function refreshTokenFlow() {

    const refresh = getRefreshToken();
    if (!refresh) return false;

    const response = await fetch(API_BASE + "/auth/refresh", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken: refresh })
    });

    if (!response.ok) return false;

    const result = await response.json();
    if (!result.success) return false;

    setTokens(result.data.accessToken, result.data.refreshToken);
    return true;
}
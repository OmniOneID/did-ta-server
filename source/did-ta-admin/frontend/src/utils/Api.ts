const API_BASE_URL = "/admin/v1";

const requestApi = (
  endpoint: string,
  method: "GET" | "POST" | "PUT" | "DELETE",
  body?: any
): Promise<{ url: string; data: any }> => {
  const fullUrl = `${API_BASE_URL}/${endpoint}`;
  const options: RequestInit = {
    method,
    headers: {
      "Content-Type": "application/json",
    },
    body: body ? JSON.stringify(body) : undefined,
  };

  return new Promise((resolve, reject) => {
    fetch(fullUrl, options)
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        // return response.json().then((data) => resolve({ url: fullUrl, data }));
        return response.text().then((text) => {
          let data = {};
          if (text) {
            try {
              data = JSON.parse(text);
            } catch (error) {
              console.warn("Failed to parse JSON response:", error);
            }
          }
          resolve({ url: fullUrl, data });
        });
      })
      .catch((error) => reject(error));
  });
};

export const getData = (endpoint: string): Promise<{ url: string; data: any }> => {
  return requestApi(endpoint, "GET");
};

export const postData = (endpoint: string, body: any): Promise<{ url: string; data: any }> => {
  return requestApi(endpoint, "POST", body);
};

export const putData = (endpoint: string, body: any): Promise<{ url: string; data: any }> => {
  return requestApi(endpoint, "PUT", body);
};

export const deleteData = (endpoint: string): Promise<{ url: string; data: any }> => {
  return requestApi(endpoint, "DELETE");
};

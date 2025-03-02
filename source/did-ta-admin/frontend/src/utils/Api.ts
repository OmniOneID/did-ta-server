const requestApi = (
  baseUrl: string,
  endpoint: string,
  method: "GET" | "POST" | "PUT" | "DELETE",
  body?: any
): Promise<{ url: string; data: any }> => {
  const fullUrl = `${baseUrl}/${endpoint}`;

  const isFormData = body instanceof FormData;

  const options: RequestInit = {
    method,
    headers: isFormData
      ? undefined
      : {
          "Content-Type": "application/json",
        },
        body: isFormData ? body : body ? JSON.stringify(body) : undefined,
  };

  return new Promise((resolve, reject) => {
    fetch(fullUrl, options)
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
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

export const getData = (baseUrl: string, endpoint: string): Promise<{ url: string; data: any }> => {
  return requestApi(baseUrl, endpoint, "GET");
};

export const postData = (baseUrl: string, endpoint: string, body: any): Promise<{ url: string; data: any }> => {
  return requestApi(baseUrl, endpoint, "POST", body);
};

export const putData = (baseUrl: string, endpoint: string, body: any): Promise<{ url: string; data: any }> => {
  return requestApi(baseUrl, endpoint, "PUT", body);
};

export const deleteData = (baseUrl: string, endpoint: string): Promise<{ url: string; data: any }> => {
  return requestApi(baseUrl, endpoint, "DELETE");
};

export const uploadData = (baseUrl: string, endpoint: string, formData: FormData): Promise<{ url: string; data: any }> => {
  return requestApi(baseUrl, endpoint, "POST", formData);
};

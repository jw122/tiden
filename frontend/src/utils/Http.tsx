import axios from "axios";

// TODO: move to env
const SERVER_URL = "http://localhost:8080";

const axiosInstance = axios.create({
  baseURL: SERVER_URL,
  timeout: 60000,
});

export default axiosInstance;

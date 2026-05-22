import { LoggerService } from "@/services";
import axios, { AxiosError, RawAxiosHeaders } from "axios";

export abstract class ServiceAdapter {

    private readonly adapterConfig;

    protected constructor(config: { baseUrl: string }) {

        if (!config.baseUrl) throw new Error("Base URL is required");

        this.adapterConfig = {
            baseUrl: config.baseUrl
        };

    }

    async makeHTTPRequest(
        method: string,
        endpoint: string,
        data?: Record<string, unknown>,
        headers?: RawAxiosHeaders
    ) {

        let url = this.adapterConfig.baseUrl + endpoint;

        if (method.toUpperCase() === 'GET') {
            url = this.withQueryParams(url, data);
            data = undefined;
        }

        try {

            const response = await axios({ method, url, data, headers });

            return {
                status: response.status,
                body: response.data
            };

        } catch (error) {

            const axiosError = error as AxiosError;

            const errorResponse = axiosError?.response;

            LoggerService.log("\nERROR:", axiosError);

            return {
                status: errorResponse?.status || 500,
                body: errorResponse?.data || { message: axiosError.message }
            };
        }
    }

    private withQueryParams(url: string, data?: Record<string, unknown>) {

        if (!data) return url;

        const params = new URLSearchParams();

        Object.entries(data).forEach(([ key, value ]) => {
            if (value !== undefined && value !== null) params.append(key, String(value));
        });

        return `${url}?${params.toString()}`;
    }

}

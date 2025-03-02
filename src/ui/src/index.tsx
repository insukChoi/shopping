import React from 'react';
import {createRoot} from 'react-dom/client';
import {MutationCache, QueryCache, QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {App, ConfigProvider, Layout} from "antd";
import "antd/dist/reset.css";
import {showError} from '@/utils/alert';
import MyHeader from '@/component/MyHeader';
import Sidebar from "@/component/Siderbar";
import Router from './Router.jsx'
import {BrowserRouter} from "react-router-dom";

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 0,
            retry: false
        }
    },
    mutationCache: new MutationCache({
            onError: (error) => {
                showError(String(error))
            }
        }
    ),
    queryCache: new QueryCache({
            onError: (error) => {
                showError(String(error))
            }
        }
    ),
});

const root = createRoot(document.getElementById("root") as HTMLElement);

root.render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <ConfigProvider>
                <BrowserRouter>
                    <Layout style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
                        <MyHeader />
                        <Layout style={{ display: 'flex', flex: 1, overflow: 'hidden' }}>
                            <Sidebar />
                            <Layout.Content style={{ padding: '24px', minHeight: 'calc(100vh - 64px)', flex: 1, overflow: 'auto', marginLeft: 270 }}>
                                <Router />
                            </Layout.Content>
                        </Layout>
                    </Layout>
                </BrowserRouter>
            </ConfigProvider>
        </QueryClientProvider>
    </React.StrictMode>
)
;

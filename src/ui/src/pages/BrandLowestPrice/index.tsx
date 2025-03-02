import React from "react";
import {Space, Table, Typography} from "antd";
import {useBrandsLowestPriceSearchQuery} from "@/pages/BrandLowestPrice/_query";

export default function BrandLowestPrice() {
    const { data, isFetching } = useBrandsLowestPriceSearchQuery();

    if (isFetching) {
        return <Typography.Text>Loading...</Typography.Text>;
    }

    if (!data || !data.lowestPrice) {
        return <Typography.Text type="danger">데이터를 불러오는 중 오류가 발생했습니다.</Typography.Text>;
    }

    const columns = [
        { title: "카테고리", dataIndex: "categoryName", key: "categoryName" },
        { title: "가격", dataIndex: "price", key: "price", render: (price: number) => price.toLocaleString() }
    ];

    return (
        <Space direction="vertical" style={{ width: "100%" }}>
            <Typography.Title level={3}>최저가 브랜드: {data.lowestPrice.brandName}</Typography.Title>
            <Table
                dataSource={data.lowestPrice.categories}
                columns={columns}
                rowKey="categoryName"
                pagination={false}
                bordered
            />
            <Typography.Title level={4} style={{ textAlign: "right", marginTop: "16px" }}>
                총액: {data.lowestPrice.totalPrice.toLocaleString()} 원
            </Typography.Title>
        </Space>
    );
}

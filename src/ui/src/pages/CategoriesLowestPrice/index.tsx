import React from "react";
import {Spin, Table, Typography} from "antd";
import {useCategoriesLowestPriceSearchQuery} from "@/pages/CategoriesLowestPrice/_query";

export default function CategoriesLowestPrice() {
    const { data, isFetching } = useCategoriesLowestPriceSearchQuery();

    if (isFetching) {
        return <Spin size="large" style={{ display: "flex", justifyContent: "center", marginTop: 50 }} />;
    }

    if (!data || !data.contents) {
        return <Typography.Text type="danger">데이터를 불러오는 중 오류가 발생했습니다.</Typography.Text>;
    }


    const columns = [
        {title: "카테고리", dataIndex: "categoryName", key: "categoryName"},
        {title: "브랜드", dataIndex: "brandName", key: "brandName"},
        {title: "가격", dataIndex: "price", key: "price", render: (price: number) => price.toLocaleString()}
    ];

    return (
        <>
            <Table
                dataSource={data.contents}
                columns={columns}
                rowKey="categoryName"
                pagination={false}
            />
            <Typography.Title level={4} style={{textAlign: "right", marginTop: "16px"}}>
                총액: {data.totalPrice.toLocaleString()} 원
            </Typography.Title>
        </>
    );

}

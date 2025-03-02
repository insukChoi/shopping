import React, {useState} from "react";
import {Button, Input, Space, Table, Typography} from "antd";
import {useCategoriesMinMaxPriceSearchQuery} from "@/pages/CategoriesMinMaxPrice/_query";

export default function CategoriesMinMaxPrice() {
    const [categoryName, setCategoryName] = useState("");
    const [searchCategory, setSearchCategory] = useState("");
    const { data, isFetching, refetch } = useCategoriesMinMaxPriceSearchQuery(searchCategory, { enabled: false });

    const handleSearch = () => {
        if (!categoryName.trim()) {
            return;
        }
        setSearchCategory(categoryName);
        setTimeout(() => refetch(), 0);
    };

    const columns = [
        { title: "브랜드", dataIndex: "brandName", key: "brandName" },
        { title: "가격", dataIndex: "price", key: "price", render: (price: number) => price.toLocaleString() }
    ];

    const priceData = data && data.lowestPrice && data.highestPrice ? [
        { key: 'lowest', brandName: data.lowestPrice.brandName, price: data.lowestPrice.price },
        { key: 'highest', brandName: data.highestPrice.brandName, price: data.highestPrice.price }
    ] : [];

    return (
        <Space direction="vertical" style={{ width: "100%" }}>
            <Typography.Title level={3}>카테고리 가격 조회</Typography.Title>
            <Space>
                <Input
                    placeholder="카테고리명을 입력하세요"
                    value={categoryName}
                    onChange={(e) => setCategoryName(e.target.value)}
                    onPressEnter={handleSearch}
                />
                <Button type="primary" onClick={handleSearch}>조회</Button>
            </Space>
            {isFetching ? (
                <Typography.Text>Loading...</Typography.Text>
            ) : data && data.categoryName ? (
                <>
                    <Typography.Title level={4}>{data.categoryName} 가격 비교</Typography.Title>
                    <Typography.Text>최저가격: {data.lowestPrice.brandName} ({data.lowestPrice.price.toLocaleString()} 원)</Typography.Text>
                    <Typography.Text>최고가격: {data.highestPrice.brandName} ({data.highestPrice.price.toLocaleString()} 원)</Typography.Text>
                    <Table
                        dataSource={priceData}
                        columns={columns}
                        rowKey="key"
                        pagination={false}
                        bordered
                    />
                </>
            ) : (
                <Typography.Text>카테고리명을 입력해주세요</Typography.Text>
            )}
        </Space>
    );
}

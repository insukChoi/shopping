import React from "react";
import {Menu, MenuProps} from "antd";
import Sider from "antd/lib/layout/Sider";
import {useNavigate} from "react-router-dom";
import {useQueryClient} from "@tanstack/react-query";

const Sidebar: React.FC = () => {
    const navigate = useNavigate();
    const queryClient = useQueryClient()

    type MenuItem = Required<MenuProps>['items'][number];
    const items: MenuItem[] = [
        getItem('카테고리별 최저가격 브랜드와 가격', '/categories/lowest-price'),
        getItem('단일 브랜드의 모든 카테고리 최저가격', '/brand/lowest-price'),
        getItem('최저, 최고가격 브랜드와 가격', '/categories/prices/mix-max'),
        getItem('상품 추가/수정/삭제 관리', '/product-management')
    ]

    const onClick: MenuProps['onClick'] = (e) => {
        navigate(e.key);
        queryClient.clear();
    }

    function getItem(
        label: React.ReactNode,
        key: React.Key,
        icon?: React.ReactNode,
        children ?: MenuItem[],
        type ?: 'group',
    ): MenuItem {
        return {
            label,
            key,
            icon,
            children,
            type,
        } as MenuItem;
    }

    return (
        <Sider width={270} style={{ height: "100vh", position: "fixed", left: 0 }}>
            <Menu style={{ minHeight: '100%' }}
                  defaultSelectedKeys={["request"]}
                  onClick={onClick}
                  mode="inline"
                  items={items}
            >
            </Menu>
        </Sider>
    );
};

export default Sidebar;

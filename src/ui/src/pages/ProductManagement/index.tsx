import React, {useState} from "react";
import {Button, Form, Input, Modal, Space, Typography} from "antd";
import {useMutation} from "@tanstack/react-query";
import {shoppingApiClient} from "@/apis/cilent";
import {showSuccess} from "@/utils/alert";

const ProductManagement = () => {
    const [form] = Form.useForm();
    const [deleteForm] = Form.useForm();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [modalType, setModalType] = useState("add"); // "add", "edit"

    const addProductMutation = useMutation(
        async (values) => await shoppingApiClient.post("/shopping/v1/product", values),
        {
            onSuccess: () => {
                showSuccess("상품이 등록되었습니다.");
                setIsModalOpen(false);
                form.resetFields();
            },
        }
    );

    const updateProductMutation = useMutation(
        async (values) => await shoppingApiClient.patch("/shopping/v1/product", values),
        {
            onSuccess: () => {
                showSuccess("상품이 수정되었습니다.");
                setIsModalOpen(false);
                form.resetFields();
            },
        }
    );

    const deleteProductMutation = useMutation(
        async (product) => await shoppingApiClient.delete("/shopping/v1/product", { data: product }),
        {
            onSuccess: () => {
                showSuccess("상품이 삭제되었습니다.");
                setIsDeleteModalOpen(false);
                deleteForm.resetFields();
            },
        }
    );

    const handleOpenModal = (type, record = null) => {
        setModalType(type);
        setIsModalOpen(true);
        if (record) {
            form.setFieldsValue(record);
        } else {
            form.resetFields();
        }
    };

    const handleOpenDeleteModal = () => {
        setIsDeleteModalOpen(true);
        deleteForm.resetFields();
    };

    const handleFormSubmit = () => {
        form.validateFields()
            .then((values) => {
                if (modalType === "add") {
                    addProductMutation.mutate(values);
                } else {
                    updateProductMutation.mutate(values);
                }
            })
            .catch((error) => {
                console.error("Validation Failed:", error);
            });
    };

    const handleDeleteSubmit = () => {
        deleteForm.validateFields()
            .then((values) => {
                deleteProductMutation.mutate(values);
            })
            .catch((error) => {
                console.error("Validation Failed:", error);
            });
    };

    return (
        <Space direction="vertical" style={{ width: "100%" }}>
            <Typography.Title level={3}>상품 관리</Typography.Title>
            <Button type="primary" onClick={() => handleOpenModal("add")}>상품 추가</Button>
            <Button type="default" onClick={() => handleOpenModal("edit")}>상품 수정</Button>
            <Button danger onClick={handleOpenDeleteModal}>상품 삭제</Button>

            {/* 상품 추가/수정 모달 */}
            <Modal
                title={modalType === "add" ? "상품 추가" : "상품 수정"}
                open={isModalOpen}
                onCancel={() => setIsModalOpen(false)}
                onOk={() => form.submit()}
            >
                <Form form={form} layout="vertical" onFinish={handleFormSubmit}>
                    <Form.Item name="brandName" label="브랜드명" rules={[{ required: true, message: "브랜드명을 입력하세요." }]}>
                        <Input placeholder="브랜드명" />
                    </Form.Item>
                    <Form.Item name="categoryName" label="카테고리명" rules={[{ required: true, message: "카테고리명을 입력하세요." }]}>
                        <Input placeholder="카테고리명" />
                    </Form.Item>
                    <Form.Item name="price" label="가격" rules={[{ required: true, message: "가격을 입력하세요." }]}>
                        <Input type="number" placeholder="가격" />
                    </Form.Item>
                </Form>
            </Modal>

            {/* 상품 삭제 모달 */}
            <Modal
                title="상품 삭제"
                open={isDeleteModalOpen}
                onCancel={() => setIsDeleteModalOpen(false)}
                onOk={() => deleteForm.submit()}
            >
                <Form form={deleteForm} layout="vertical" onFinish={handleDeleteSubmit}>
                    <Form.Item name="brandName" label="브랜드명" rules={[{ required: true, message: "브랜드명을 입력하세요." }]}>
                        <Input placeholder="브랜드명" />
                    </Form.Item>
                    <Form.Item name="categoryName" label="카테고리명" rules={[{ required: true, message: "카테고리명을 입력하세요." }]}>
                        <Input placeholder="카테고리명" />
                    </Form.Item>
                </Form>
            </Modal>
        </Space>
    );
};

export default ProductManagement;



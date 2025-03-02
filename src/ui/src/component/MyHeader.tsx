import React from "react";
import {Layout} from "antd";

const MyHeader: React.FC = () => {
    return (
        <Layout.Header style={{ background: "#001529", color: "white", textAlign: "left", padding: "0 20px" }}>
            <h2 style={{ margin: 0, color: "white" }}>쇼핑 화면</h2>
        </Layout.Header>
    );
};

export default MyHeader;

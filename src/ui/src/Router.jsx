import {Route, Routes} from "react-router-dom";
import Home from "@/pages/Home";
import CategoriesLowestPrice from "@/pages/CategoriesLowestPrice";
import BrandLowestPrice from "@/pages/BrandLowestPrice";
import CategoriesMinMaxPrice from "@/pages/CategoriesMinMaxPrice";
import ProductManagement from "@/pages/ProductManagement";

export default function BasicRouter() {
    return (
        <Routes>
            <Route path="/" element={<Home/>}/>
            <Route path="/categories/lowest-price" element={<CategoriesLowestPrice/>}/>
            <Route path="/brand/lowest-price" element={<BrandLowestPrice/>}/>
            <Route path="/categories/prices/mix-max" element={<CategoriesMinMaxPrice/>}/>
            <Route path="/product-management" element={<ProductManagement/>}/>
        </Routes>
    );
}

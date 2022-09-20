import React, { useEffect } from "react";
import Router from "./Router";
import { connect } from "react-redux";
import "./components/@vuexy/rippleButton/RippleButton";
import "react-toastify/dist/ReactToastify.css";
import "react-perfect-scrollbar/dist/css/styles.css";
import "./assets/scss/plugins/tables/_agGridStyleOverride.scss";
import "prismjs/themes/prism-tomorrow.css";
import "antd/dist/antd.css";
import { ToastContainer, toast, Flip } from "react-toastify";
import "flatpickr/dist/themes/light.css";
import "./assets/scss/plugins/forms/flatpickr/flatpickr.scss";
const App = (props) => {
    useEffect(() => {
        if (props.error) {
            toast.error(props.error, { transition: Flip });
        }
    });
    return (
        <>
            <ToastContainer />
            <Router />
        </>
    );
};

const mapStateToProps = (state) => {
    return {
        error: state.auth.login.error,
    };
};

export default connect(mapStateToProps)(App);

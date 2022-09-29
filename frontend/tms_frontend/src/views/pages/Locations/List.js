import React from "react";
import { Card, CardBody } from "reactstrap";
import { AgGridReact } from "ag-grid-react";
import { Button } from "reactstrap";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
// import Moment from "react-moment";
import { Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";

class Locations extends React.Component {
    state = {
        data: [],
        loading: false,
        paginationPageSize: 10,
        defaultColDef: {
            sortable: true,
            resizable: true,
            suppressMenu: true,
            wrapText: true,
            autoHeight: true,
            filter: true,
            tooltip: (params) => {
                return params.value;
            },
        },
        columnDefs: [
            {
                headerName: "ID",
                field: "id",
                minWidth: 100,
                maxWidth: 300,
            },
            {
                headerName: "ANSI",
                field: "ansi",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "Name",
                field: "name",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "Parent ANSI",
                field: "parentAnsi",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "First Time Zone",
                field: "firstTimeZone",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "Second Time Zone",
                field: "secondTimeZone",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "Parent Time Zone",
                field: "parentTimeZone",
                minWidth: 100,
                flex: 1,
            },
        ],
    };
    componentDidUpdate(prevProps, prevState) {
        if (this.state.page !== prevState.page) {
            this.setState({
                loading: true,
            });
            fetch(process.env.REACT_APP_BASE_URL + `/location/list`, {
                headers: {
                    Authorization: this.props.token,
                },
            })
                .then((res) => res.json())
                .then((data) => {
                    this.setState({
                        data,
                        loading: false,
                    });
                });
        }
    }
    componentDidMount() {
        this.setState({
            loading: true,
        });
        fetch(process.env.REACT_APP_BASE_URL + "/location/list", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    data,
                    loading: false,
                });
            });
    }

    antIcon = (<LoadingOutlined style={{ fontSize: 44 }} spin />);

    pageChanged = (page) => {
        this.setState({
            page: page - 1,
        });
    };

    render() {
        const { columnDefs, defaultColDef } = this.state;
        return (
            <>
                <Card className="overflow-hidden agGrid-card">
                    <div className="d-flex justify-content-between mt-2 mx-2 mb-1">
                        <h3>All locations list</h3>
                        <div>
                            <Link
                                to="/locations/new"
                                className="d-flex align-items-center text-white"
                            >
                                <Button
                                    color="success"
                                    className="d-flex align-items-center"
                                    type="button"
                                    size="sm"
                                >
                                    <Icon.Plus size={20} /> Add new location
                                </Button>
                            </Link>
                        </div>
                    </div>
                    <CardBody className="py-0 no-pagination">
                        {this.state.loading ? (
                            <Spin
                                indicator={this.antIcon}
                                style={{
                                    height: "calc(100vh - 20rem)",
                                    width: "100%",
                                    display: "flex",
                                    justifyContent: "center",
                                    alignItems: "center",
                                }}
                            />
                        ) : (
                            <>
                                <div className="ag-theme-material w-100 ag-grid-table pb-2">
                                    <AgGridReact
                                        enableCellTextSelection="true"
                                        reactNext={true}
                                        rowSelection="multiple"
                                        defaultColDef={defaultColDef}
                                        columnDefs={columnDefs}
                                        rowData={this.state.data}
                                        colResizeDefault={"shift"}
                                        animateRows={true}
                                        pagination={false}
                                        floatingFilter={true}
                                        pivotPanelShow="always"
                                    />
                                </div>
                            </>
                        )}
                    </CardBody>
                </Card>
            </>
        );
    }
}
const mapStateToProps = (state) => {
    return {
        token: state.auth.login.token,
    };
};
export default connect(mapStateToProps)(Locations);

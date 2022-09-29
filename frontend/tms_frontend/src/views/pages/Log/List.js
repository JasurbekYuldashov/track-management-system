import React from "react";
import { Card, CardBody } from "reactstrap";
import { AgGridReact } from "ag-grid-react";
import { Button } from "reactstrap";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import Moment from "react-moment";
import { Pagination, Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";

class Logs extends React.Component {
    state = {
        data: [],
        page: null,
        total: 1,
        loading: false,
        paginationPageSize: 10,
        defaultColDef: {
            sortable: true,
            resizable: true,
            suppressMenu: false,
            tooltip: (params) => {
                return params.value;
            },
        },
        columnDefs: [
            {
                headerName: "Username",
                field: "username",
                minWidth: 100,
                maxWidth: 300,
            },
            {
                headerName: "Table name",
                field: "tableName",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "Entity id",
                field: "entityId",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "Action",
                field: "actionType",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "Time",
                field: "time",
                minWidth: 100,
                flex: 1,
                cellRendererFramework: (params) => {
                    return <Moment fromNow>{params.data.timeStamp}</Moment>;
                },
            },
        ],
    };
    componentDidUpdate(prevProps, prevState) {
        if (this.state.page !== prevState.page) {
            this.setState({
                loading: true,
            });
            fetch(
                process.env.REACT_APP_BASE_URL +
                    `/admin/log?sort=id,DESC&size=40&page=${this.state.page}`,
                {
                    headers: {
                        Authorization: this.props.token,
                    },
                }
            )
                .then((res) => res.json())
                .then((data) => {
                    let dataToShow = [];
                    data.content.forEach((el) => {
                        let elToShow = {
                            username: el.username,
                            tableName: el.tableName,
                            actionType: el.actionType,
                            entityId: el.entityId,
                            timeStamp: el.timeStamp,
                        };
                        dataToShow.push(elToShow);
                    });
                    this.setState({
                        data: dataToShow,
                        loading: false,
                    });
                });
        }
    }
    componentDidMount() {
        this.setState({
            loading: true,
        });
        fetch(
            process.env.REACT_APP_BASE_URL + "/admin/log?sort=id,DESC&size=40",
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.json())
            .then((data) => {
                let dataToShow = [];
                data.content.forEach((el) => {
                    let elToShow = {
                        username: el.username,
                        tableName: el.tableName,
                        actionType: el.actionType,
                        entityId: el.entityId,
                        timeStamp: el.timeStamp,
                    };
                    dataToShow.push(elToShow);
                });
                this.setState({
                    data: dataToShow,
                    loading: false,
                    total: data.totalElements,
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
                    <CardBody className="py-0 no-pagination">
                        <div className="d-flex justify-content-between my-2">
                            <h3>Logs</h3>
                        </div>
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
                                <div className="ag-theme-material w-100 ag-grid-table with-pagination">
                                    <AgGridReact
                                        enableCellTextSelection="true"
                                        rowSelection="multiple"
                                        defaultColDef={defaultColDef}
                                        columnDefs={columnDefs}
                                        rowData={this.state.data}
                                        colResizeDefault={"shift"}
                                        animateRows={true}
                                        pagination={false}
                                        pivotPanelShow="always"
                                    />
                                </div>
                                <Pagination
                                    current={this.state.page + 1}
                                    total={this.state.total}
                                    onChange={this.pageChanged}
                                    pageSize={40}
                                    style={{
                                        textAlign: "center",
                                        margin: "20px",
                                        marginBottom: "50px",
                                    }}
                                />
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
export default connect(mapStateToProps)(Logs);

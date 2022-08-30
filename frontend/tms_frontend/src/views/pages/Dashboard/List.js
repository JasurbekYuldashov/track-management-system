import React from "react";
import {
    Card,
    Col,
    CardBody,
    Nav,
    NavItem,
    NavLink, Button,
} from "reactstrap";
import classnames from "classnames";
import {AgGridReact} from "ag-grid-react";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import {connect} from "react-redux";
import {Link} from "react-router-dom";
import {Spin} from "antd";
import {LoadingOutlined} from "@ant-design/icons";
import {changeDashboardState} from "../../../redux/actions/utility";
import Select from "react-select";
import Backdrop from "@mui/material/Backdrop";
import {Card as CardMui, CardActions as CardActionsMui, CardContent as CardContentMui, TextField} from "@mui/material";
// import ButtonMui from '@mui/material/Button';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import CloseIcon from '@mui/icons-material/Close';
import moment from "moment";


class Trips extends React.Component {
    state = {
        data: [],
        active: null,
        team: null,
        teams: [],
        loading: false,
        selectedTeam: null,
        owned_companies: [],

        defaultColDef: {
            sortable: true,
            resizable: true,
            filter: true,
            tooltip: (params) => {
                return params.value;
            },
        },
        open: false,
        truckId: null,

        columnDefs: [
            {
                headerName: "#",
                field: "index",
                maxWidth: 50,
                flex: 1,
                filter: false,
                cellStyle: function (params) {
                    return {
                        fontWeight: "500",
                    };
                },
            },
            {
                headerName: "Unit #",
                field: "unitNumber",
                minWidth: 120,
                flex: 1,
                cellRendererFramework: function (params) {
                    return (
                        <Link className="link-in-table" to={`/unit/view/${params.value}`}>
                            {params.value}
                        </Link>
                    );
                },
            },
            {
                headerName: "Driver",
                field: "driverOne",
                minWidth: 200,
                flex: 1,
                filter: true,
                cellRendererFramework: function (params) {
                    return (
                        <div className="drivers-col">
                            <Link
                                style={{
                                    color: params.data.teamColor !== "#FFFFFF" && "white",
                                }}
                                className="link-in-table"
                                to={`/driver/view/${params.data.driverOneId}`}
                            >
                                {params.data.driverOne}
                            </Link>
                            {params.data.driverTwoId !== null && (
                                <Link
                                    style={{
                                        color: params.data.teamColor !== "#FFFFFF" && "white",
                                    }}
                                    className="link-in-table"
                                    to={`/driver/view/${params.data.driverTwoId}`}
                                >
                                    {params.data.driverTwo}
                                </Link>
                            )}
                        </div>
                    );
                },
                cellStyle: function (params) {
                    return {
                        backgroundColor: params.data.teamColor,
                    };
                },
            },
            {
                headerName: "Phone numbers",
                field: "driverOne",
                minWidth: 150,
                flex: 1,
                filter: true,
                cellRendererFramework: function (params) {
                    return (
                        <div className="drivers-col">
                            <Link
                                className="link-in-table"
                                to={`/driver/view/${params.data.driverOneId}`}
                            >
                                {params.data.driverOnePhoneNumber}
                            </Link>
                            {params.data.driverTwoId !== null && (
                                <Link
                                    className="link-in-table"
                                    to={`/driver/view/${params.data.driverTwoId}`}
                                >
                                    {params.data.driverTwoPhoneNumber}
                                </Link>
                            )}
                        </div>
                    );
                },
                cellStyle: function (params) {
                    return {
                        backgroundColor: '#ffffff',
                    };
                },
            },
            {
                headerName: "UT",
                field: "typeOfUnit",
                filter: true,
                minWidth: 60,
                maxWidth: 60,
                flex: 1,
                tooltip: (params) => {
                    return params.value;
                },
            },
            {
                headerName: "DT",
                field: "typeOfDriver",
                filter: true,
                minWidth: 60,
                maxWidth: 60,
                flex: 1,
                tooltip: (params) => {
                    return params.value;
                },
            },
            {
                headerName: "Unit Status",
                field: "unitStatus",
                filter: true,
                minWidth: 90,
                maxWidth: 90,
                flex: 1,
                cellStyle: function (params) {
                    return {
                        fontSize: "13px",
                        color: params.data.unitStatusColor ? "white" : "black",
                        backgroundColor: params.data.unitStatusColor
                            ? params.data.unitStatusColor
                            : "white",
                        textAlign: "center",
                        textTransform: "uppercase",
                    };
                },
                cellRendererFramework: (params) => {
                    return (
                        <div>
                            <div
                                // aria-controls={this.state.openMenu ? 'basic-menu' : undefined}
                                // aria-haspopup="true" aria-expanded={this.state.openMenu ? 'true' : undefined}
                                style={{width: '78px'}}
                                onClick={($event) => this.handleMenuClick($event, params.data.truckId, params.data.unitStatusId)}
                            >{params.data.unitStatus}</div>
                        </div>
                    )
                }
            },
            {
                headerName: "From",
                field: "from",
                filter: true,
                minWidth: 200,
                flex: 1,
                tooltip: (params) => {
                    return params.value;
                },
            },
            {
                headerName: "To",
                field: "to",
                filter: true,
                minWidth: 200,
                flex: 1,
                tooltip: (params) => {
                    return params.value;
                },
            },
            {
                headerName: "Destination time",
                field: "endTime",
                filter: true,
                minWidth: 130,
                flex: 1,
            },
            {
                headerName: "Load #",
                field: "loadNumber",
                filter: true,
                minWidth: 170,
                flex: 1,
                cellRendererFramework: function (params) {
                    return (
                        <Link
                            className="link-in-table"
                            to={`/loads/edit/${params.data.loadId}`}
                        >
                            {params.value}
                        </Link>
                    );
                },
            },
            {
                headerName: "Weekly gross",
                field: "calc",
                filter: true,
                minWidth: 170,
                flex: 1,
                cellRendererFramework: function (params) {
                    return (
                        <Link
                            className="link-in-table"
                            to={`/loads/edit/${params.data.loadId}`}
                        >
                            {params.value}
                        </Link>
                    );
                },
            },
            {
                headerName: "notes",
                field: "notes",
                filter: true,
                minWidth: 250,
                flex: 1,
                // tooltip: (params) => {
                //     return params.value;
                // },
                cellRendererFramework: (params) => {
                    return (
                        <div className="notes-col">
                            {!params.data.notes ?
                                <Button.Ripple
                                    style={{width: 225}}
                                    className="d-flex align-items-center"
                                    type="button"
                                    onClick={() => this.handleToggle(params.data.truckId)}
                                >
                                    Add note
                                </Button.Ripple> :
                                <div onClick={() => this.handleToggle(params.data.truckId)}>
                                    {params.data.notes}
                                </div>


                            }
                        </div>
                    );
                },
            },
        ],
        notesData: null,
        note: '',
        notesTotalElements: null,
        notesTotalPages: null,
        unitStatuses: null,
        anchorEl: null,
        openMenu: false,
        statusChangeId: null,
        currentUnitStatuses: null
    };

    updateSum = async (index) => {

    }
    updateInfo = async (tab = 0, team = 0) => {

        this.setState({
            loading: true,
        });
        let a = moment().isoWeekday(-6); // ISO day of the week with 1 being Monday and 7 being Sunday.
        a.toDate().getTime()
        fetch(
            `/dashboard?sort=id,DESC&size=10000${
                tab ? `&currentEmployerId=${tab}` : ""
            }${team ? `&teamId=${team}` : ""}&startTime=${a.toDate().getTime()}&endTime=${moment().toDate().getTime()}`,
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.json())
            .then((data) => {
                if (this.state.active !== tab) return;
                let dataToShow = [];
                data.data.forEach((el, i) => {
                    console.log(el)
                    let elToShow = {
                        index: i + 1,
                        unitNumber: el.number,
                        driverOneId: el.driverOneId,
                        driverTwoId: el.driverTwoId,
                        driverOne: el.driverOne,
                        driverOnePhoneNumber: el.driverOnePhoneNumber,
                        driverTwo: el.driverTwo,
                        driverTwoPhoneNumber: el.driverTwoPhoneNumber,
                        typeOfUnit: el.typeOfUnit,
                        typeOfDriver: el.typeOfDriver,
                        unitStatus: el.unitStatus,
                        unitStatusColor: el.unitStatusColor,
                        teamColor: el.teamColor,
                        driverStatus: el.driverStatus,
                        driverStatusColor: el.driverStatusColor,
                        from: el.from,
                        to: el.to,
                        loadNumber: el.loadNumber,
                        notes: el.notes,
                        endTime: el.endTime,
                        loadId: el.loadId,
                        truckId: el.truckId,
                        unitStatusId: el.unitStatusId,
                        calc: el.calc
                    };
                    dataToShow.push(elToShow);
                });
                this.setState({
                    data: dataToShow,
                    loading: false,
                    verySelectedTeam: team
                });
            });
    };

    handleMenuClick = (event, truckId, unitStatusId) => {
        this.setState({
            statusChangeId: truckId
        })

        const currentUnitStatuses = this.state.unitStatuses?.filter(x => x.id !== unitStatusId)
        this.setState({anchorEl: event.currentTarget, openMenu: true, currentUnitStatuses: currentUnitStatuses})
    }

    handleMenuClose = () => {
        this.setState({anchorEl: null, openMenu: false})
    }

    changeUnitStatus = (status) => {
        fetch(`/unit/update_status/${this.state.statusChangeId}/${status.id}`, {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({}),
            method: "PUT",
        })
            .then(() => {
                this.setState({anchorEl: null, openMenu: false})
                this.updateInfo()
            })
            .catch(() => {
                this.setState({anchorEl: null, openMenu: false})
            })

    }

    toggle = (tab) => {
        this.props.changeDashboardState(tab, this.state.team);
        if (this.state.active !== tab) {
            this.setState({active: tab});
            this.updateInfo(tab, this.state.team);
        }
    };

    handleToggle = (id) => {
        this.setState({
            open: !this.state.open,
            truckId: id
        });
        this.getNotes(id)
    };

    handleClose = () => {
        this.setState({
            open: false,
            truckId: null,
            note: null,
            notesData: []
        });
    };

    addNote = () => {
        fetch("/truck_notes", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify({
                content: this.state.note,
                truckId: this.state.truckId
            })
        })
            .then((res) => res.json())
            .then((data) => {
                this.getNotes(this.state.truckId)
                this.setState({
                    note: '',
                });
                this.updateInfo()

            });
    };

    getUnitStatuses = () => {
        fetch("/unit/context", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "GET",
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    unitStatuses: data.unit_statuses
                })
            });
    };

    getNotes = (truckId) => {
        fetch(`/truck_notes?truck_id=${truckId}&page=0&size=1000000`, {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "GET",
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    notesData: data.data.notes,
                    notesTotalPages: data.data.totalPages,
                    notesTotalElements: data.data.totalElements
                });
            });
    }

    handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            // console.log('enter press here! ')
            this.addNote()
        }
    }

    handleChangeNote = (event) => {
        this.setState({
            note: event.target.value
        })
    }

    toggleTeam = (value) => {
        if (value == null) {
            this.props.changeDashboardState(this.state.active, null);
            if (this.state.team !== null) {
                this.setState({team: null, selectedTeam: null});
                this.updateInfo(this.state.active, null);
            }
        } else {
            this.props.changeDashboardState(this.state.active, value.value);
            if (this.state.team !== value.value) {
                this.setState({team: value.value, selectedTeam: value});
                this.updateInfo(this.state.active, value.value);
            }
        }
    };

    componentDidMount() {

        fetch("/owned_company/all_for_dashboard?sort=id,DESC&size=10000", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            }
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    owned_companies: data,
                });
            });
        fetch("/dashboard/context", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            }
        })
            .then((res) => res.json())
            .then((data) => {
                let dataToShow = [];
                data.teams.forEach((el) => {
                    let elToShow = {
                        value: el.id,
                        label: el.name,
                    };
                    dataToShow.push(elToShow);
                    this.setState({
                        unit_types: data.unit_types,
                        ownership_types: data.ownership_types,
                        unitStatuses: data.unit_statuses,
                        teams: dataToShow,
                    });
                    let selectedTeam = null;
                    if (this.props.team !== null) {
                        selectedTeam = dataToShow.find((item) => {
                            return item.value == this.props.team;
                        });
                    }

                    this.setState({
                        active: this.props.tab,
                        team: this.props.team,
                        selectedTeam,
                    });
                });
            });

        this.updateInfo(this.props.tab, this.props.team);
        this.getUnitStatuses();
    }

    render() {
        const {columnDefs, defaultColDef} = this.state;
        return (
            <>
                <Card className="overflow-hidden agGrid-card">
                    <div className="d-flex justify-content-between ml-1 mt-1 mr-1">
                        <div className="d-flex align-items-center ">
                            <Nav pills className="dashboard-tabs">
                                <NavItem>
                                    <NavLink
                                        style={{height: 38}}
                                        className={classnames(
                                            {
                                                customActive: this.state.active === 0,
                                            },
                                            "d-flex align-items-center"
                                        )}
                                        onClick={() => {
                                            this.toggle(0);
                                        }}
                                    >
                                        <h4 className="mb-0 ml-1 mr-1">All</h4>
                                    </NavLink>
                                </NavItem>
                                {this.state.owned_companies.map((item) => (
                                    <NavItem>
                                        <NavLink
                                            style={{height: 38}}
                                            className={classnames(
                                                {
                                                    customActive: this.state.active == item.id,
                                                },
                                                "d-flex align-items-center"
                                            )}
                                            onClick={() => {
                                                this.toggle(item.id);
                                            }}
                                        >
                                            {item.logoFileId ? (
                                                <img
                                                    src={`${window.location.origin}/file/${item.logoFileId}`}
                                                    style={{maxHeight: 38, maxWidth: 120}}
                                                />
                                            ) : (
                                                <h4 className="mb-0 ml-1 mr-1">{item.abbreviation}</h4>
                                            )}
                                        </NavLink>
                                    </NavItem>
                                ))}
                            </Nav>
                        </div>
                        <div className="d-flex" style={{width: 200}}>
                            <Col md="12" className="pr-0">
                                <Select
                                    className="React"
                                    classNamePrefix="select"
                                    name="color"
                                    placeholder="Select team"
                                    options={this.state.teams}
                                    isClearable={true}
                                    value={this.state.selectedTeam}
                                    onChange={this.toggleTeam}
                                    theme={(theme) => ({
                                        ...theme,
                                        colors: {
                                            ...theme.colors,
                                            primary50: "#fe810b",
                                            primary25: "rgb(253, 179, 46)",
                                            primary: "rgb(253, 179, 46)",
                                        },
                                    })}
                                />
                            </Col>
                        </div>
                    </div>
                    {this.state.loading ? (
                        <Spin
                            indicator={<LoadingOutlined style={{fontSize: 44}} spin/>}
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
                            <CardBody className="py-0 no-pagination">
                                <div className="ag-theme-material w-100 my-1 ag-grid-table">
                                    <AgGridReact
                                        enableCellTextSelection="true"
                                        rowSelection="multiple"
                                        defaultColDef={defaultColDef}
                                        columnDefs={columnDefs}
                                        rowData={this.state.data}
                                        colResizeDefault={"shift"}
                                        animateRows={true}
                                        floatingFilter={true}
                                    />
                                </div>
                            </CardBody>
                        </>
                    )}
                </Card>
                {/* <Notes open={this.state} handleClose={()=>this.handleClose()}></Notes> */}
                <Backdrop
                    transitionDuration={350}
                    sx={{color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1}}
                    open={this.state.open}
                    id="backdrop"
                    // onClick={this.handleClose}
                >
                    <CardMui
                        style={{marginLeft: "auto", height: "100%"}}
                        sx={{minWidth: 475}}
                    >
                        <CardContentMui>
                            <div style={{alignItems: 'center', display: 'flex'}}>
                                <CloseIcon onClick={this.handleClose}/>
                                <h1 style={{marginLeft: '15px', marginBottom: 0}}>Notes</h1>
                            </div>
                            <TextField style={{width: '100%'}} value={this.state.note} onChange={this.handleChangeNote}
                                       onKeyPress={this.handleKeyPress} label="Add note" variant="standard"/>
                            <div style={{
                                height: (parseInt(document.getElementById('backdrop')?.clientHeight) - 140) + 'px',
                                overflow: 'auto'
                            }}>
                                {this.state.notesData?.map((item, index) => {
                                    return (
                                        <div style={{
                                            border: '1px solid silver',
                                            borderRadius: '10px',
                                            padding: '10px',
                                            marginBottom: '10px',
                                            marginTop: index === 0 ? '10px' : '0'
                                        }}>
                                            <div style={{
                                                display: 'flex',
                                                justifyContent: 'space-between',
                                                flexWrap: 'wrap'
                                            }}>
                                                <p>{item.author}</p>
                                                <p>{new Date(item.postedDate).toLocaleString()}</p>
                                            </div>
                                            <div style={{fontStyle: 'italic'}}>
                                                {item.content}
                                            </div>
                                        </div>

                                    )
                                })}
                            </div>
                        </CardContentMui>
                        <CardActionsMui>

                        </CardActionsMui>
                    </CardMui>
                </Backdrop>
                <Menu
                    id="basic-menu"
                    anchorEl={this.state.anchorEl}
                    open={this.state.openMenu}
                    onClose={this.handleMenuClose}
                    MenuListProps={{
                        'aria-labelledby': 'basic-button',
                    }}
                >
                    {
                        this.state.currentUnitStatuses?.map(status => {
                            return (
                                <MenuItem onClick={() => this.changeUnitStatus(status)}>{status.name}</MenuItem>
                            )
                        })
                    }
                </Menu>
            </>
        );
    }
}

const mapStateToProps = (state) => {
    // console.log(state);


    return {
        token: state.auth.login.token,
        tab: state.utility.data.dashboardTab,
        team: state.utility.data.dashboardTeam,
    };
};
export default connect(mapStateToProps, {changeDashboardState})(Trips);

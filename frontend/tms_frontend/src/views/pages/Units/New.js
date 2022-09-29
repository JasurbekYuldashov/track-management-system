import React from "react";
import {
    // Form,
    Form,
    Button,
    FormGroup,
    Input,
    // Label,
    CustomInput,
    // Row,
    Col,
    Card,
    CardBody,
    // CardTitle,
    CardHeader,
    // Button
} from "reactstrap";
import Select from "react-select";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import {
    // ToastContainer,
    toast,
    // Slide,
    // Zoom,
    Flip,
    // Bounce,
} from "react-toastify";
import Flatpickr from "react-flatpickr";
import InfiniteFileUploader from "../../../components/main/infiniteFileUploader";
class NewUnit extends React.Component {
    state = {
        states: [],
        status: 1,
        type: 1,
        teams: [],
        team: null,
        teamSelected: null,
        drivers: [],
        driver: null,
        driverId: null,
        secondDriver: null,
        secondDriverId: null,
        unit_types: [],
        ownership_types: [],
        newFiles: [],
        prevFiles: [],
        fileIds: new Map(),
        currentFile: 0,
        unitStatuses: [],
    };
    newFile = () => {
        let newFiles = this.state.newFiles;
        let currentFile = this.state.currentFile;
        newFiles.push(currentFile);
        this.setState({
            newFiles,
            currentFile: currentFile + 1,
        });
    };

    deleteFile = (item) => {
        let newFiles = this.state.newFiles;
        let fileIds = this.state.fileIds;
        fileIds.delete(item);
        this.setState({
            fileIds,
        });
        let index = newFiles.indexOf(item);
        if (index > -1) {
            newFiles.splice(index, 1);
        }
        this.setState({
            newFiles,
        });
    };

    deletePrevFile = (item) => {
        let prevFiles = this.state.prevFiles;
        let fileIds = this.state.fileIds;
        fileIds.delete(item);
        this.setState({
            fileIds,
        });
        let obj = prevFiles.find((el) => el.id === item);
        let index = prevFiles.indexOf(obj);
        if (index > -1) {
            prevFiles.splice(index, 1);
        }
        this.setState({
            prevFiles,
        });
    };

    uploadFile = (file, item) => {
        let formData = new FormData();
        formData.append("file", file);
        if (file === undefined) {
            return;
        }
        fetch(process.env.REACT_APP_BASE_URL + "/file/upload", {
            headers: {
                Authorization: this.props.token,
            },
            method: "POST",
            body: formData,
        })
            .then((res) => res.json())
            .then((data) => {
                let newFiles = this.state.newFiles;
                let newFile = newFiles.indexOf(item);
                newFiles[newFile] = data;
                let fileIds = this.state.fileIds;
                fileIds.set(data, file.name);
                this.setState({ fileIds, newFiles });
            });
    };

    newUnit = () => {
        let obj = Object.create(null);
        for (let [k, v] of this.state.fileIds) {
            obj[k] = v;
        }

        let data = {
            eldUnTil:
                this.state.status == 7 && document.querySelector("#eldUnTil")
                    ? Date.parse(document.querySelector("#eldUnTil").value)
                    : null,
            notes: document.querySelector("#notes").value,
            number: document.querySelector("#number").value,
            files: obj,
            unitStatusId: this.state.status,
            unitTypeId: this.state.type,
            driverId: this.state.driverId,
            secondDriverId: this.state.secondDriverId,
            teamId: this.state.team,
            ownershipTypeId: parseInt(
                document.querySelector("#ownershipType").value
            ),
            initialLocation: {
                stateProvince: parseInt(document.querySelector("#state").value),
                city: document.querySelector("#city").value,
                street: document.querySelector("#street").value,
            },
            vin: document.querySelector("#vin").value,
            make: document.querySelector("#make").value,
            model: document.querySelector("#model").value,
            description: document.querySelector("#description").value,
            year: parseInt(document.querySelector("#year").value),
            licensePlateNumber: document.querySelector("#licensePlateNumber")
                .value,
            licensePlateExpiration: document.querySelector(
                "#licensePlateExpiration"
            ).value,
            inspectionStickerExpiration: document.querySelector(
                "#inspectionStickerExpiration"
            ).value,
            registrationExpirationTime:
                document.querySelector("#registrationExpirationTime").value &&
                Date.parse(
                    document.querySelector("#registrationExpirationTime").value
                ),
            annualInspectionExpirationTime:
                document.querySelector("#annualInspectionExpirationTime")
                    .value &&
                Date.parse(
                    document.querySelector("#annualInspectionExpirationTime")
                        .value
                ),
        };
        fetch(process.env.REACT_APP_BASE_URL + "/unit/new", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("Unit successfuly added", { transition: Flip });
                window.history.back();
            } else {
                toast.error("Something went wrong", { transition: Flip });
                res.text();
            }
        });
    };

    componentDidMount() {
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/driver/list?sort=id,DESC&size=10000`,
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.json())
            .then((data) => {
                let drivers = data.content.map((item) => {
                    return {
                        value: item.id,
                        label: `${item.firstName} ${item.lastName}`,
                    };
                });
                this.setState({ drivers });
            });
        fetch(process.env.REACT_APP_BASE_URL + "/state_province/all", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) =>
                this.setState({
                    states: data,
                })
            );
        fetch(process.env.REACT_APP_BASE_URL + "/unit/context", {
            headers: {
                Authorization: this.props.token,
            },
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
                });
            });
    }

    teamChange = (value) => {
        if (value == null) {
            this.setState({
                team: null,
                teamSelected: null,
            });
        } else {
            this.setState({
                team: value.value,
                teamSelected: value,
            });
        }
    };
    render() {
        return (
            <>
                <Card>
                    <CardHeader>
                        <h3 className="mb-0">Adding a new Unit</h3>
                    </CardHeader>
                    <CardBody>
                        <Form className="d-flex">
                            <div style={{ width: "50%", marginRight: 20 }}>
                                <h4>Main</h4>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Number*</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="number" />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>VIN</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="vin" />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Type*</span>
                                    </Col>
                                    <Col md="8">
                                        <CustomInput
                                            type="select"
                                            name="select"
                                            id="type"
                                            onChange={(e) =>
                                                this.setState({
                                                    type: e.target.value,
                                                })
                                            }
                                        >
                                            {this.state.unit_types.map(
                                                (item) => (
                                                    <option
                                                        key={item.id}
                                                        value={item.id}
                                                    >
                                                        {item.name}
                                                    </option>
                                                )
                                            )}
                                        </CustomInput>
                                    </Col>
                                </FormGroup>

                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Ownership type</span>
                                    </Col>
                                    <Col md="8">
                                        <CustomInput
                                            type="select"
                                            name="select"
                                            id="ownershipType"
                                        >
                                            {this.state.ownership_types.map(
                                                (item) => (
                                                    <option
                                                        key={item.id}
                                                        value={item.id}
                                                    >
                                                        {item.name}
                                                    </option>
                                                )
                                            )}
                                        </CustomInput>
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Status</span>
                                    </Col>
                                    <Col
                                        md={this.state.status === 7 ? "4" : "8"}
                                    >
                                        <CustomInput
                                            type="select"
                                            name="select"
                                            id="status"
                                            onChange={(e) =>
                                                this.setState({
                                                    status: e.target.value,
                                                })
                                            }
                                            value={this.state.status}
                                        >
                                            {this.state.unitStatuses.map(
                                                (item) => (
                                                    <option
                                                        key={item.id}
                                                        value={item.id}
                                                    >
                                                        {item.name}
                                                    </option>
                                                )
                                            )}
                                        </CustomInput>
                                    </Col>
                                    {this.state.status === 7 && (
                                        <Col md="4">
                                            <Flatpickr
                                                id="eldUnTil"
                                                className="form-control"
                                                placeholder="ELD Until"
                                                data-enable-time
                                                options={{
                                                    dateFormat: "Z",
                                                    altInput: true,
                                                    altFormat: "m-d-Y H:i",
                                                }}
                                            />
                                        </Col>
                                    )}
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Driver</span>
                                    </Col>
                                    <Col md="8">
                                        <Select
                                            className="React"
                                            classNamePrefix="select"
                                            name="color"
                                            options={this.state.drivers}
                                            value={this.state.driver}
                                            isClearable={true}
                                            onChange={(value) => {
                                                if (value !== null) {
                                                    this.setState({
                                                        driverId: value.value,
                                                        driver: value,
                                                    });
                                                } else {
                                                    this.setState({
                                                        driverId: null,
                                                        driver: null,
                                                    });
                                                }
                                            }}
                                            theme={(theme) => ({
                                                ...theme,
                                                colors: {
                                                    ...theme.colors,
                                                    primary50: "#fe810b",
                                                    primary25:
                                                        "rgb(253, 179, 46)",
                                                    primary:
                                                        "rgb(253, 179, 46)",
                                                },
                                            })}
                                        />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Second driver(optimal)</span>
                                    </Col>
                                    <Col md="8">
                                        <Select
                                            className="React"
                                            classNamePrefix="select"
                                            name="color"
                                            value={this.state.secondDriver}
                                            options={this.state.drivers}
                                            isClearable={true}
                                            onChange={(val) => {
                                                if (val == null) {
                                                    this.setState({
                                                        secondDriver: null,
                                                        secondDriverId: null,
                                                    });
                                                } else {
                                                    this.setState({
                                                        secondDriver: val,
                                                        secondDriverId:
                                                            val.value,
                                                    });
                                                }
                                            }}
                                            theme={(theme) => ({
                                                ...theme,
                                                colors: {
                                                    ...theme.colors,
                                                    primary50: "#fe810b",
                                                    primary25:
                                                        "rgb(253, 179, 46)",
                                                    primary:
                                                        "rgb(253, 179, 46)",
                                                },
                                            })}
                                        />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Team</span>
                                    </Col>
                                    <Col md="8">
                                        <Select
                                            className="React"
                                            classNamePrefix="select"
                                            name="color"
                                            options={this.state.teams}
                                            isClearable={true}
                                            value={this.state.teamSelected}
                                            onChange={this.teamChange}
                                            theme={(theme) => ({
                                                ...theme,
                                                colors: {
                                                    ...theme.colors,
                                                    primary50: "#fe810b",
                                                    primary25:
                                                        "rgb(253, 179, 46)",
                                                    primary:
                                                        "rgb(253, 179, 46)",
                                                },
                                            })}
                                        />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Initial Location</span>
                                    </Col>
                                    <Col md="8">
                                        <CustomInput
                                            type="select"
                                            name="select"
                                            id="state"
                                        >
                                            {this.state.states.map((item) => (
                                                <option
                                                    key={item.id}
                                                    value={item.id}
                                                >
                                                    {item.name}
                                                </option>
                                            ))}
                                        </CustomInput>
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>City*</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="city" />
                                    </Col>
                                </FormGroup>

                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Street</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="street" />
                                    </Col>
                                </FormGroup>
                            </div>
                            <div style={{ width: "50%", marginRight: 20 }}>
                                <h4>Optional</h4>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Make</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="make" />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Model</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="model" />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Description</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="description" />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Year</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="year" />
                                    </Col>
                                </FormGroup>

                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>License Plate Number</span>
                                    </Col>
                                    <Col md="8">
                                        <Input
                                            type="text"
                                            id="licensePlateNumber"
                                        />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>License Plate Expiration</span>
                                    </Col>
                                    <Col md="8">
                                        <Flatpickr
                                            id="licensePlateExpiration"
                                            className="form-control"
                                        />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>
                                            Inspection Sticker Expiration
                                        </span>
                                    </Col>
                                    <Col md="8">
                                        <Flatpickr
                                            id="inspectionStickerExpiration"
                                            className="form-control"
                                        />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>
                                            Annual Inspection Expiration Time
                                        </span>
                                    </Col>
                                    <Col md="8">
                                        <Flatpickr
                                            id="annualInspectionExpirationTime"
                                            className="form-control"
                                            data-enable-time
                                            options={{
                                                dateFormat: "Z",
                                                altInput: true,
                                                altFormat: "m-d-Y H:i",
                                            }}
                                        />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>
                                            Registration Expiration Time
                                        </span>
                                    </Col>
                                    <Col md="8">
                                        <Flatpickr
                                            id="registrationExpirationTime"
                                            className="form-control"
                                            data-enable-time
                                            options={{
                                                dateFormat: "Z",
                                                altInput: true,
                                                altFormat: "m-d-Y H:i",
                                            }}
                                        />
                                    </Col>
                                </FormGroup>

                                <FormGroup>
                                    <Input
                                        type="textarea"
                                        id="notes"
                                        placeholder="Notes"
                                        maxLength="1000"
                                        style={{ minHeight: 98 }}
                                    />
                                </FormGroup>
                            </div>
                        </Form>
                    </CardBody>
                </Card>
                <InfiniteFileUploader
                    newFiles={this.state.newFiles}
                    prevFiles={this.state.prevFiles}
                    deletePrevFile={this.deletePrevFile}
                    newFile={this.newFile}
                    deleteFile={this.deleteFile}
                    uploadFile={this.uploadFile}
                />
                <div className="d-flex justify-content-center mt-2">
                    <Button
                        color="success"
                        className="d-flex align-items-center"
                        type="button"
                        onClick={() => this.newUnit()}
                    >
                        <Icon.Check size={22} /> Save unit
                    </Button>
                </div>
            </>
        );
    }
}
const mapStateToProps = (state) => {
    return {
        token: state.auth.login.token,
    };
};
export default connect(mapStateToProps)(NewUnit);

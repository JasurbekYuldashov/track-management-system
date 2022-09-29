import React from "react";
import {
    // Form,
    Form,
    Button,
    FormGroup,
    Input,
    Label,
    CustomInput,
    Row,
    Col,
    Card,
    CardBody,
    CardTitle,
    CardHeader,
    // Button
} from "reactstrap";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import {
    ToastContainer,
    toast,
    Slide,
    Zoom,
    Flip,
    Bounce,
} from "react-toastify";
import ImageUploader from "react-images-upload";
import InfiniteFileUploader from "../../../components/main/infiniteFileUploader";
class NewCompany extends React.Component {
    state = {
        states: [],
        customer_types: [],
        logoFileId: null,
        newFiles: [],
        prevFiles: [],
        fileIds: new Map(),
        currentFile: 0,
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

    newCompany = () => {
        let obj = Object.create(null);
        for (let [k, v] of this.state.fileIds) {
            obj[k] = v;
        }

        let data = {
            logoFileId: this.state.logoFileId,
            files: obj,
            abbreviation: document.querySelector("#abbreviation").value,
            alternatePhone: document.querySelector("#alternatePhone").value,
            alternatePhoneExtensionNumber:
                document.querySelector("#alternatePhone").value,
            aptSuiteOther: document.querySelector("#alternatePhone").value,
            city: document.querySelector("#city").value,
            name: document.querySelector("#companyName").value,
            contact: document.querySelector("#contact").value,
            email: document.querySelector("#email").value,
            fax: document.querySelector("#fax").value,
            motorCarrierNumber: document.querySelector("#motorCarrierNumber")
                .value,
            notes: document.querySelector("#notes").value,
            phoneExtensionNumber: document.querySelector(
                "#phoneExtensionNumber"
            ).value,
            phoneNumber: document.querySelector("#phoneNumber").value,
            stateProvinceId: parseInt(
                document.querySelector("#stateProvince").value
            ),
            street: document.querySelector("#street").value,
            taxId: document.querySelector("#taxId").value,
            webSite: document.querySelector("#webSite").value,
            zipCode: document.querySelector("#zipCode").value,
        };
        fetch(process.env.REACT_APP_BASE_URL + "/owned_company/create", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("Company successfuly added", {
                    transition: Flip,
                });
                window.history.back();
            } else {
                toast.error("Something went wrong", { transition: Flip });

                res.text();
            }
        });
    };

    onDrop = (pictureFiles, pictureDataURLs) => {
        if (pictureFiles.length === 0) {
            return;
        }
        let formData = new FormData();
        formData.append("file", pictureFiles[0], pictureFiles[0].name);

        fetch(process.env.REACT_APP_BASE_URL + "/file/upload", {
            headers: {
                Authorization: this.props.token,
            },
            method: "POST",
            body: formData,
        })
            .then((res) => res.json())
            .then((data) =>
                this.setState({
                    logoFileId: data,
                })
            );
    };

    componentDidMount() {
        fetch(process.env.REACT_APP_BASE_URL + "/company/context", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) =>
                this.setState({
                    customer_types: data.customer_types,
                    states: data.state_province,
                })
            );
    }

    render() {
        return (
            <>
                <Card>
                    <CardHeader>
                        <h3 className="mb-0">Adding a new Company</h3>
                    </CardHeader>
                    <CardBody>
                        <div className="d-flex">
                            <div style={{ flex: 1, marginRight: 20 }}>
                                <Form>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Company Name*</span>
                                        </Col>
                                        <Col md="8">
                                            <Input
                                                type="text"
                                                id="companyName"
                                            />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Abbreviation*</span>
                                        </Col>
                                        <Col md="8">
                                            <Input
                                                type="text"
                                                id="abbreviation"
                                            />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Street</span>
                                        </Col>
                                        <Col md="8">
                                            <Input type="text" id="street" />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>City*</span>
                                        </Col>
                                        <Col md="8">
                                            <Input type="text" id="city" />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>State*</span>
                                        </Col>
                                        <Col md="8">
                                            <CustomInput
                                                type="select"
                                                name="select"
                                                id="stateProvince"
                                            >
                                                {this.state.states.map(
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
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Zip Code</span>
                                        </Col>
                                        <Col md="8">
                                            <Input type="text" id="zipCode" />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Phone Number</span>
                                        </Col>
                                        <Col md="8">
                                            <Input
                                                type="text"
                                                id="phoneNumber"
                                            />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Phone Extension number</span>
                                        </Col>
                                        <Col md="8">
                                            <Input
                                                type="text"
                                                id="phoneExtensionNumber"
                                            />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Alternate Phone</span>
                                        </Col>
                                        <Col md="8">
                                            <Input
                                                type="text"
                                                id="alternatePhone"
                                            />
                                        </Col>
                                    </FormGroup>
                                </Form>
                            </div>
                            <div style={{ width: "50%" }}>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>
                                            Alternate Phone Extension number
                                        </span>
                                    </Col>
                                    <Col md="8">
                                        <Input
                                            type="text"
                                            id="alternatePhoneExtensionNumber"
                                        />
                                    </Col>
                                </FormGroup>

                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Fax</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="fax" />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Email</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="email" />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Website</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="webSite" />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Contact</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="contact" />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Notes</span>
                                    </Col>
                                    <Col md="8">
                                        <Input
                                            type="textarea"
                                            id="notes"
                                            maxLength="1000"
                                        />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Motor Carrier Number</span>
                                    </Col>
                                    <Col md="8">
                                        <Input
                                            type="text"
                                            id="motorCarrierNumber"
                                        />
                                    </Col>
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Tax ID (EIN#)</span>
                                    </Col>
                                    <Col md="8">
                                        <Input type="text" id="taxId" />
                                    </Col>
                                </FormGroup>
                            </div>
                        </div>
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
                <ImageUploader
                    withIcon={true}
                    withPreview={true}
                    buttonText="Choose image"
                    onChange={this.onDrop}
                    imgExtension={[".jpg", ".png"]}
                    maxFileSize={5242880}
                    singleImage
                    label="Upload Logo"
                />
                <div className="d-flex justify-content-center mt-2">
                    <Button
                        color="success"
                        className="d-flex align-items-center"
                        type="button"
                        onClick={() => this.newCompany()}
                    >
                        <Icon.Check size={22} /> Save
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
export default connect(mapStateToProps)(NewCompany);

import React from "react";
import {
  // Form,
  Form,
  Button,
  FormGroup,
  Input,
  CustomInput,
  Col,
  Card,
  CardBody,
  CardHeader,
  // Button
} from "reactstrap";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { toast, Flip } from "react-toastify";
import ImageUploader from "react-images-upload";
import InfiniteFileUploader from "../../../components/main/infiniteFileUploader";
import { Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
class NewCompany extends React.Component {
  state = {
    states: [],
    customer_types: [],
    logoFileId: null,
    logoFromPast: false,
    newFiles: [],
    prevFiles: [],
    fileIds: new Map(),
    currentFile: 0,
    loading: true,
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
    let obj = prevFiles.find((el) => el.id == item);
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
    if (file == undefined) {
      return;
    }
    fetch("/file/upload", {
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

  deleteLogo = () => {
    this.setState({ logoFileId: null });
  };
  editCompany = () => {
    let obj = Object.create(null);
    for (let [k, v] of this.state.fileIds) {
      obj[k] = v;
    }

    let data = {
      id: this.props.match.params.id,
      files: obj,
      logoFileId: this.state.logoFileId,
      abbreviation: document.querySelector("#abbreviation").value,
      alternatePhone: document.querySelector("#alternatePhone").value,
      alternatePhoneExtensionNumber: document.querySelector("#alternatePhone")
        .value,
      aptSuiteOther: document.querySelector("#alternatePhone").value,
      city: document.querySelector("#city").value,
      name: document.querySelector("#companyName").value,
      contact: document.querySelector("#contact").value,
      email: document.querySelector("#email").value,
      fax: document.querySelector("#fax").value,
      motorCarrierNumber: document.querySelector("#motorCarrierNumber").value,
      notes: document.querySelector("#notes").value,
      phoneExtensionNumber: document.querySelector("#phoneExtensionNumber")
        .value,
      phoneNumber: document.querySelector("#phoneNumber").value,
      stateProvinceId: parseInt(document.querySelector("#stateProvince").value),
      street: document.querySelector("#street").value,
      taxId: document.querySelector("#taxId").value,
      webSite: document.querySelector("#webSite").value,
      zipCode: document.querySelector("#zipCode").value,
    };

    fetch("/owned_company/edit", {
      headers: {
        Authorization: this.props.token,
        "Content-Type": "application/json",
      },
      method: "PUT",
      body: JSON.stringify(data),
    }).then((res) => {
      if (res.ok) {
        toast.success("Company successfuly edited", { transition: Flip });
        window.history.back();
      } else {
        toast.error("Something went wrong", { transition: Flip });

        res.text();
      }
    });
  };
  onDrop = (pictureFiles, pictureDataURLs) => {
    if (pictureFiles.length == 0) {
      this.setState({
        logoFileId: null,
      });
      return;
    }
    let formData = new FormData();
    formData.append("file", pictureFiles[0], pictureFiles[0].name);

    fetch("/file/upload", {
      headers: {
        Authorization: this.props.token,
      },
      method: "POST",
      body: formData,
    })
      .then((res) => res.json())
      .then((data) => {
        this.setState({
          logoFileId: data,
          logoFromPast: false,
        });
      });
  };
  componentDidMount() {
    fetch("/company/context", {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        this.setState({
          customer_types: data.customer_types,
          states: data.state_province,
        });
        fetch(`/owned_company/${this.props.match.params.id}`, {
          headers: {
            Authorization: this.props.token,
          },
        })
          .then((res) => res.json())
          .then((data) => {
            this.setState({
              logoFileId: data.logoFileId,
              logoFromPast: true,
              loading: false,
            });
            document.querySelector("#abbreviation").value = data.abbreviation;
            document.querySelector("#alternatePhone").value =
              data.alternatePhone;
            document.querySelector("#alternatePhone").value =
              data.alternatePhoneExtensionNumber;
            document.querySelector("#alternatePhone").value =
              data.alternatePhone;
            document.querySelector("#city").value = data.city;
            document.querySelector("#companyName").value = data.name;
            document.querySelector("#contact").value = data.contact;
            document.querySelector("#email").value = data.email;
            document.querySelector("#fax").value = data.fax;
            document.querySelector("#motorCarrierNumber").value =
              data.motorCarrierNumber;
            document.querySelector("#notes").value = data.notes;
            document.querySelector("#phoneExtensionNumber").value =
              data.phoneExtensionNumber;
            document.querySelector("#phoneNumber").value = data.phoneNumber;
            document.querySelector("#stateProvince").value =
              data.stateProvinceId;
            document.querySelector("#street").value = data.street;
            document.querySelector("#taxId").value = data.taxId;
            document.querySelector("#webSite").value = data.webSite;
            document.querySelector("#zipCode").value = data.zipCode;

            if (data.files !== null) {
              let fileIds = new Map();
              let prevFiles = [];
              for (let key in data.files) {
                prevFiles.push({ id: parseInt(key), name: data.files[key] });
                fileIds.set(parseInt(key), data.files[key]);
              }
              this.setState({ fileIds, prevFiles });
            }
          });
      });
  }
  render() {
    return (
      <>
        <Card>
          <CardHeader>
            <h3 className="mb-0">Edit Company</h3>
          </CardHeader>
          <CardBody>
            {this.state.loading ? (
              <Spin
                indicator={<LoadingOutlined style={{ fontSize: 44 }} spin />}
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
                <div className="d-flex">
                  <div style={{ flex: 1, marginRight: 20 }}>
                    <Form>
                      <FormGroup className="align-items-center" row>
                        <Col md="4">
                          <span>Company Name*</span>
                        </Col>
                        <Col md="8">
                          <Input type="text" id="companyName" />
                        </Col>
                      </FormGroup>
                      <FormGroup className="align-items-center" row>
                        <Col md="4">
                          <span>Abbreviation*</span>
                        </Col>
                        <Col md="8">
                          <Input type="text" id="abbreviation" />
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
                          <span>State*</span>
                        </Col>
                        <Col md="8">
                          <CustomInput
                            type="select"
                            name="select"
                            id="stateProvince"
                          >
                            {this.state.states.map((item) => (
                              <option key={item.id} value={item.id}>
                                {item.name}
                              </option>
                            ))}
                          </CustomInput>
                        </Col>
                      </FormGroup>
                      <FormGroup className="align-items-center" row>
                        <Col md="4">
                          <span>Zip Code</span>
                        </Col>
                        <Col md="8">
                          <Input type="text" id="zipCode" />
                        </Col>
                      </FormGroup>
                      <FormGroup className="align-items-center" row>
                        <Col md="4">
                          <span>Phone Number</span>
                        </Col>
                        <Col md="8">
                          <Input type="text" id="phoneNumber" />
                        </Col>
                      </FormGroup>
                      <FormGroup className="align-items-center" row>
                        <Col md="4">
                          <span>Phone Extension number</span>
                        </Col>
                        <Col md="8">
                          <Input type="text" id="phoneExtensionNumber" />
                        </Col>
                      </FormGroup>
                      <FormGroup className="align-items-center" row>
                        <Col md="4">
                          <span>Alternate Phone</span>
                        </Col>
                        <Col md="8">
                          <Input type="text" id="alternatePhone" />
                        </Col>
                      </FormGroup>
                    </Form>
                  </div>
                  <div style={{ width: "50%" }}>
                    <FormGroup className="align-items-center" row>
                      <Col md="4">
                        <span>Alternate Phone Extension number</span>
                      </Col>
                      <Col md="8">
                        <Input type="text" id="alternatePhoneExtensionNumber" />
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
                        <Input type="textarea" id="notes" maxLength="1000" />
                      </Col>
                    </FormGroup>
                    <FormGroup className="align-items-center" row>
                      <Col md="4">
                        <span>Motor Carrier Number</span>
                      </Col>
                      <Col md="8">
                        <Input type="text" id="motorCarrierNumber" />
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
              </>
            )}
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

        {this.state.logoFileId && this.state.logoFromPast ? (
          <Card
            className="d-flex align-items-center justify-content-center"
            style={{ flexDirection: "initial", paddingBottom: 15 }}
          >
            <Button.Ripple
              style={{ width: 225 }}
              className="mt-1"
              type="button"
              onClick={() =>
                window.open(
                  `${window.location.origin}/file/${this.state.logoFileId}`,
                  "_blank"
                )
              }
            >
              Download Logo
            </Button.Ripple>
            <Button.Ripple
              className="btn-icon mt-1"
              color="red"
              type="button"
              onClick={() => this.deleteLogo()}
            >
              <Icon.Trash2 />
            </Button.Ripple>
          </Card>
        ) : (
          <ImageUploader
            withIcon={true}
            withPreview={true}
            buttonText="Choose image"
            onChange={this.onDrop}
            imgExtension={[".jpg", ".png", ".svg"]}
            maxFileSize={5242880}
            singleImage
            label="Upload Logo"
          />
        )}
        <div className="d-flex justify-content-center mt-2">
          <Button
            color="success"
            className="d-flex align-items-center"
            type="button"
            onClick={() => this.editCompany()}
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

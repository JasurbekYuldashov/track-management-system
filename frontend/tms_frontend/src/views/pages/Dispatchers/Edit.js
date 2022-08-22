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
import {connect} from "react-redux";
import {toast, Flip} from "react-toastify";
import {Spin} from "antd";
import {LoadingOutlined} from "@ant-design/icons";
import Select from "react-select";

class EditUser extends React.Component {

    state = {
        states: [],
        customer_types: [],
        logoFileId: null,
        userName: [],
        loading: true,
    };

    newUser = () => {
        let data = {
            id: parseInt(this.props.match.params.id),
            username: document.querySelector("#username").value,
            password: document.querySelector("#password").value,
            roleId: parseInt(document.querySelector("#role").value),
            name: document.querySelector("#name").value,
            email: document.querySelector("#email").value,
            phone: document.querySelector("#phoneNumber").value,
            visibleIds: this.state.selectedCompanies,
            visibleTeamIds: this.state.selectedTeams
        };

        fetch("/admin/edit_user", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "PUT",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("User successfuly edited", {transition: Flip});
                window.history.back();
            } else {
                toast.error("Something went wrong", {transition: Flip});
                res.text();
            }
        });
    };
    handleCompanySelect = (array) =>{

        let letMappedCompanies = []
        if(array != null)
            letMappedCompanies = array.map((element) => {
                return element.value;
            })

        this.setState({selectedCompanies : letMappedCompanies, selectedCompanyOptions: array})
    }

    handleTeamSelect = (array) => {
        let mappedTeams = [];
        if(array != null){
            mappedTeams = array.map((team) => {
                return team.value
            })
        }

        this.setState({selectedTeams: mappedTeams, selectedTeamOptions: array})
    }

    componentDidMount() {
        fetch(`/admin/${this.props.match.params.id}`, {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    loading: false,
                });

                let selectedOptions = data.visibleIds.map((id) => {
                    for(let i = 0; i < data.availableCompanies.length; i++){
                        if(data.availableCompanies[i].id === id)
                            return (
                                {
                                    value: id,
                                    label: data.availableCompanies[i].name
                                }
                            )
                    }
                });
                let mappedCompanyData = data.availableCompanies.map((company) => {
                    return (
                        {
                            value: company.id,
                            label: company.name
                        }
                    )
                })

                let selectedTeamOptions = data.visibleTeamIds.map((id) => {
                    for(let i = 0; i < data.availableTeams.length; i++){
                        if(data.availableTeams[i].id === id)
                            return (
                                {
                                    value: id,
                                    label: data.availableTeams[i].name
                                }
                            )
                    }
                })

                let mappedTeamData = data.availableTeams.map((team) => {
                    return (
                        {
                            value: team.id,
                            label: team.name
                        }
                    )
                })

                this.setState({
                    selectedCompanyOptions: selectedOptions,
                    selectedTeamOptions: selectedTeamOptions,
                    availableCompanies: mappedCompanyData,
                    availableTeams: mappedTeamData,
                    selectedCompanies: data.visibleIds,
                    selectedTeams: data.visibleTeamIds
                })
                document.querySelector("#username").value = data.username;
                document.querySelector("#role").value = data.role;
                document.querySelector("#name").value = data.name;
                document.querySelector("#email").value = data.email;
                document.querySelector("#phoneNumber").value = data.phone;
            });
    }

    render() {
        return (
            <>
                <Card>
                    <CardHeader>
                        <h3 className="mb-0">Editing user</h3>
                    </CardHeader>
                    <CardBody>
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
                                <div className="d-flex">
                                    <div style={{flex: 1, marginRight: 20}}>
                                        <Form>
                                            <FormGroup className="align-items-center" row>
                                                <Col md="4">
                                                    <span>Username</span>
                                                </Col>
                                                <Col md="8">
                                                    <Input type="text" id="username"/>
                                                </Col>
                                            </FormGroup>
                                            <FormGroup className="align-items-center" row>
                                                <Col md="4">
                                                    <span>Password</span>
                                                </Col>
                                                <Col md="8">
                                                    <Input type="text" id="password"/>
                                                </Col>
                                            </FormGroup>
                                            <FormGroup className="align-items-center" row>
                                                <Col md="4">
                                                    <span>Role</span>
                                                </Col>
                                                <Col md="8">
                                                    <CustomInput type="select" name="select" id="role">
                                                        <option key={2} value={2}>
                                                            updater
                                                        </option>
                                                        <option key={3} value={3}>
                                                            dispatcher
                                                        </option>
                                                    </CustomInput>
                                                </Col>
                                            </FormGroup>
                                            <FormGroup className="align-items-center" row>
                                                <Col md="4">
                                                    <span>Visible companies</span>
                                                </Col>
                                                <Col>
                                                    <Select
                                                        isMulti
                                                        name="colors"
                                                        className="visible"
                                                        classNamePrefix="select"
                                                        options={this.state.availableCompanies}
                                                        onChange={this.handleCompanySelect}
                                                        value={this.state.selectedCompanyOptions}
                                                    />
                                                </Col>
                                            </FormGroup>
                                            <FormGroup className="align-items-center" row>
                                                <Col md="4">
                                                    <span>Visible teams</span>
                                                </Col>
                                                <Col>
                                                    <Select
                                                        isMulti
                                                        name="colors"
                                                        className="visible"
                                                        classNamePrefix="select"
                                                        options={this.state.availableTeams}
                                                        onChange={this.handleTeamSelect}
                                                        value={this.state.selectedTeamOptions}
                                                    />
                                                </Col>
                                            </FormGroup>
                                            <FormGroup className="align-items-center" row>
                                                <Col md="4">
                                                    <span>Name</span>
                                                </Col>
                                                <Col md="8">
                                                    <Input type="text" id="name"/>
                                                </Col>
                                            </FormGroup>
                                            <FormGroup className="align-items-center" row>
                                                <Col md="4">
                                                    <span>Email</span>
                                                </Col>
                                                <Col md="8">
                                                    <Input type="text" id="email"/>
                                                </Col>
                                            </FormGroup>
                                            <FormGroup className="align-items-center" row>
                                                <Col md="4">
                                                    <span>Phone Number</span>
                                                </Col>
                                                <Col md="8">
                                                    <Input type="text" id="phoneNumber"/>
                                                </Col>
                                            </FormGroup>
                                        </Form>
                                    </div>
                                    <div style={{width: "50%"}}></div>
                                </div>
                                <Button
                                    color="success"
                                    className="d-flex align-items-center"
                                    type="button"
                                    onClick={() => this.newUser()}
                                >
                                    <Icon.Check size={22}/> Save
                                </Button>
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
export default connect(mapStateToProps)(EditUser);

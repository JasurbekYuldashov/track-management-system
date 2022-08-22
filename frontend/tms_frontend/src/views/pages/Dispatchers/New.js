
import React from "react";

import {
    Form,
    Button,
    FormGroup,
    Input,
    CustomInput,
    Col,
    Card,
    CardBody,
    CardHeader,
} from "reactstrap";

import * as Icon from "react-feather";

import {connect} from "react-redux";

import {
    toast,
    Flip,
} from "react-toastify";

import Select from "react-select";

class NewCompany extends React.Component {
    state = {
        states: [],
        customer_types: [],
        logoFileId: null,
        userName: [],
        availableCompanies: [],
        selectedCompanyOptions: [],
        availableTeams: [],
        selectedTeamOptions: []
    };

    handleSelect = (array) =>{

        let mappedCompanies = []
        if(array != null)
        mappedCompanies = array.map((element) => {
            return element.value;
        })

        this.setState({selectedCompanies : mappedCompanies, selectedCompanyOptions: array})
    }

    handleTeamSelect = (array) => {
        let mappedTeams = []
        if(array != null)
            mappedTeams = array.map((element) => {
                return element.value;
            })

        this.setState({selectedTeams: mappedTeams, selectedTeamOptions: array})
    }

    getAvailableAndSelectedCompaniesAndTeams = async () => {
        let dataForAddNewUser = await fetch('/admin/user_context', {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            }
        });
        let context = await dataForAddNewUser.json();
        return context.data;
    }

    async componentDidMount() {
        let data = await this.getAvailableAndSelectedCompaniesAndTeams()

        let mappedCompaniesData = data.companies.map((elem) => {
            return (
                {
                    value: elem.id,
                    label: elem.name
                }
            )
        })

        let mappedTeamsData = data.teams.map((elem) => {
            return (
                {
                    value: elem.id,
                    label: elem.name
                }
            )
        })

        this.setState({
            availableCompanies: mappedCompaniesData,
            availableTeams: mappedTeamsData
        });
    }

    newUser = () => {
        let data = {
            username: document.querySelector("#username").value,
            password: document.querySelector("#password").value,
            roleId: parseInt(document.querySelector("#role").value),
            name: document.querySelector("#name").value,
            email: document.querySelector("#email").value,
            phone: document.querySelector("#phoneNumber").value,
            visibleIds: this.state.selectedCompanies,
            visibleTeamIds: this.state.selectedTeams
        };

        fetch("/admin/create_user", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("User successfully added", {transition: Flip});
                window.history.back();
            } else {
                toast.error("Something went wrong", {transition: Flip});
                res.text();
            }
        });
    };

    render() {
        return (
            <>
                <Card>
                    <CardHeader>
                        <h3 className="mb-0">Adding a new Dispatcher</h3>
                    </CardHeader>
                    <CardBody>
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
                                                onChange={this.handleSelect}
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
                            <Icon.Check size={22}/> Save user
                        </Button>
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
export default connect(mapStateToProps)(NewCompany);

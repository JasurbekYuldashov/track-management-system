import React from "react";
import * as Icon from "react-feather";
const navigationConfig = [
  {
    id: "dashboard",
    title: "Dashboard",
    type: "item",
    icon: <Icon.Home size={20} />,
    permissions: ["admin", "dispatcher", "accountant"],
    navLink: "/dashboard/list",
  },
  {
    id: "trips",
    title: "Trips",
    type: "item",
    icon: <Icon.Navigation size={20} />,
    permissions: ["admin", "dispatcher", "accountant"],
    navLink: "/trips/list",
  },
  {
    id: "loads",
    title: "Loads",
    type: "item",
    icon: <Icon.Package size={20} />,
    permissions: ["admin", "dispatcher", "accountant"],
    navLink: "/loads/list",
  },
  {
    id: "drivers",
    title: "Drivers",
    type: "item",
    icon: <Icon.Users size={20} />,
    permissions: ["admin", "dispatcher", "accountant"],
    navLink: "/drivers/list",
  },
  {
    id: "units",
    title: "Units",
    type: "item",
    icon: <Icon.Truck size={20} />,
    permissions: ["admin", "dispatcher", "accountant"],
    navLink: "/units/list",
  },
  {
    id: "fleet",
    title: "Fleet",
    type: "item",
    icon: <Icon.FileText size={20} />,
    permissions: ["admin", "dispatcher", "accountant"],
    navLink: "/fleet/list",
  },
  {
    id: "reports",
    title: "Reports",
    type: "item",
    icon: <Icon.Clipboard size={20} />,
    permissions: ["admin", "accountant"],
    navLink: "/reports",
  },
  {
    id: "dispatchers",
    title: "Users",
    type: "item",
    icon: <Icon.UserPlus size={20} />,
    permissions: ["admin"],
    navLink: "/users/list",
  },
  {
    id: "companies",
    title: "Companies",
    type: "item",
    icon: <Icon.BookOpen size={20} />,
    permissions: ["admin"],
    navLink: "/companies/list",
  },
  {
    id: "customers",
    title: "Customers",
    type: "item",
    icon: <Icon.Layers size={20} />,
    permissions: ["admin"],
    navLink: "/customers/list",
  },
  {
    id: "locations",
    title: "Locations",
    type: "item",
    icon: <Icon.MapPin size={20} />,
    permissions: ["admin"],
    navLink: "/locations",
  },
  {
    id: "Logs",
    title: "Logs",
    type: "item",
    icon: <Icon.Terminal size={20} />,
    permissions: ["admin"],
    navLink: "/logs",
  },
  {
    id: "exit",
    title: "Exit",
    type: "item",
    icon: <Icon.LogOut size={20} />,
    permissions: ["admin", "dispatcher", "accountant"],
    navLink: "/exit",
  },
];

export default navigationConfig;

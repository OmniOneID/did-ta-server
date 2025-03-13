import { createTheme, ThemeOptions } from "@mui/material";
import { DataGridProps } from "@mui/x-data-grid";

const customTheme: ThemeOptions & {
  components: {
    MuiDataGrid?: {
      styleOverrides?: {
        root?: object;
        columnHeaders?: object;
        row?: object;
      };
      defaultProps?: Partial<DataGridProps>;
    };
  };
} = {
  palette: {
    mode: "light",
    background: {
      default: "#F5F5F7",
    },
  },
  typography: {
    fontFamily: '"SUIT", sans-serif',
  },
  components: {
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: "#202B45",
          color: "#FFFFFF",
          fontWeight: 400,
          fontSize: "16px",
          lineHeight: "150%",
          borderRadius: "8px",
          padding: "8px",
          marginLeft: "15px",
          height: "98%",
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          "& .MuiSvgIcon-root": {
            color: "#ffffff !important",
          },
          "&.Mui-selected": {
            backgroundColor: "#4E546B",
            "&:hover": {
              backgroundColor: "#4E546B",
            },
            "& .MuiTypography-root": {
              fontWeight: "bold !important",
            },
            "& .MuiSvgIcon-root": {
              color: "#FFFFFF !important",
            },
          },
        },
      },
    },
    MuiListItemIcon: {
      styleOverrides: {
        root: {
          color: "#ffffff",
        },
      },
    },
    MuiSvgIcon: {
      styleOverrides: {
        root: {
          color: "#ffffff",
        },
      },
    },
    MuiListItemText: {
      styleOverrides: {
        primary: {
          "&.MuiTypography-root": {
            color: "inherit",
          },
        },
      },
    },
    MuiCheckbox: {
      styleOverrides: {
        root: {
          "& .MuiSvgIcon-root": {
            color: "#FF8400",
          },
          "&.Mui-checked .MuiSvgIcon-root": {
            color: "#FF8400",
          },
        },
      },
    },
    MuiDivider: {
      styleOverrides: {
        root: {
          backgroundColor: "#4E546B",
          width: "90%",
        },
      },
    },
    MuiDataGrid: {
      styleOverrides: {
        root: {
          backgroundColor: "#FFFFFF", 
          border: "none",
        },
        columnHeaders: {
          backgroundColor: "#F5F5F7",
          color: "#333333", 
          fontWeight: "bold",
        },
        row: {
          "&:hover": {
            backgroundColor: "#F0F0F0", 
          },
        },
      },
      defaultProps: {
        disableColumnMenu: true,
        autoHeight: true,
      } as Partial<DataGridProps>,
    },
    MuiButton: {
      styleOverrides: {
        root: {
          "&.MuiButton-outlined": {
            borderColor: "#FF8400",
            color: "#FF8400",
            "&:hover": {
              backgroundColor: "rgba(255, 132, 0, 0.1)",
              borderColor: "#FF8400",
            },
          },
          "&.MuiButton-outlinedSecondary": {
            borderColor: "#000000",  
            color: "#000000",        
            backgroundColor: "#FFFFFF", 
            "&:hover": {
              backgroundColor: "#F5F5F5",
              borderColor: "#000000",
            },
          },
          "&.MuiButton-containedPrimary": {
            backgroundColor: "#FF8400",
            color: "#FFFFFF",
            "&:hover": {
              backgroundColor: "#E67500",
            },
          },
          "&.MuiButton-containedSecondary": { 
            backgroundColor: "#000000",  
            borderColor: "#000000",      
            color: "#FFFFFF",           
            "&:hover": {
              backgroundColor: "#333333", 
              borderColor: "#333333",
            },
          },
        },
      },
    },
  },
};

export default createTheme(customTheme);
